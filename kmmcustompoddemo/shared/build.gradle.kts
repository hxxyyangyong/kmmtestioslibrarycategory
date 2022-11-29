import org.jetbrains.kotlin.gradle.internal.Kapt3GradleSubplugin.Companion.findKaptConfiguration

plugins {
    kotlin("multiplatform") version "1.7.20"
    kotlin("native.cocoapods") version "1.7.20"
    id("com.android.library")
}

kotlin {
    android()
    iosX64()
//    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "13.0"
        framework {
            baseName = "shared"
        }

        specRepos {
            url("https://github.com/hxxyyangyong/yyspec.git")
        }
        pod("yytestpod"){
            version = "0.1.11"
        }
        useLibraries()
    }

    listOf(
        iosX64(),
//        iosArm64(),
//        iosSimulatorArm64()
    ).forEach {
        val platform = if (it.targetName == "iosArm64") "iphoneos" else "iphonesimulator"
        it.binaries {
            val fRootPath = "${buildDir}/cocoapods/synthetic/IOS/Pods"
            getTest(org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType.DEBUG).apply {
                linkerOpts("-L/usr/lib/swift")
                linkerOpts("-rpath","/usr/lib/swift")
                linkerOpts("-ObjC")
                linkerOpts("-L/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/lib/swift/${platform}")
                linkerOpts("-rpath","/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/lib/swift-5.0/${platform}")
                linkerOpts("-force_load","${buildDir}/cocoapods/synthetic/IOS/Pods/yytestpod/yytestpod/Classes/library/libDebugLibrary.a")
                linkerOpts("-framework", "DebugFramework","-F${fRootPath}/yytestpod/yytestpod/Classes")
            }
        }
        it.compilations["main"].kotlinOptions.freeCompilerArgs += listOf(
            "-Xexport-kdoc",
            "-Xallocator=mimalloc",
            "-Xruntime-logs=gc=info",
        )
    }

    
    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting
        val androidTest by getting
        val iosX64Main by getting
//        val iosArm64Main by getting
//        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
//            iosArm64Main.dependsOn(this)
//            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
//        val iosArm64Test by getting
//        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
//            iosArm64Test.dependsOn(this)
//            iosSimulatorArm64Test.dependsOn(this)
        }
    }
    val (deviceName,deviceUDID) = SimulatorHelp.getDeviceNameAndId()
    targets.withType(org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithSimulatorTests::class.java) {
        testRuns["test"].deviceId = deviceUDID
    }

    val testBinary = targets.getByName<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>("iosX64").binaries.getTest("DEBUG")
    val runIosTests by project.tasks.creating(SimulatorTestsTask::class) {
        dependsOn(testBinary.linkTask)
        testExecutable.set(testBinary.outputFile)
        simulatorId.set(deviceName)
    }
    tasks["check"].dependsOn(runIosTests)
}

android {
    namespace = "com.example.kmmcustompoddemo"
    compileSdk = 33
    defaultConfig {
        minSdk = 33
        targetSdk = 33
    }
}


gradle.taskGraph.whenReady {
    tasks.filter { it.name.startsWith("generateDef") }
        .forEach {
            tasks.named<org.jetbrains.kotlin.gradle.tasks.DefFileTask>(it.name).configure {
                doLast {
                    val taskSuffix = this.name.replace("generateDef", "", false)
                    val headers = when (taskSuffix) {
                        "Yytestpod" -> "TTDemo.h DebugLibrary.h NSString+librarykmm.h TTDemo+kmm.h NSString+kmm.h"
                        else -> ""
                    }
                    val compilerOpts = when (taskSuffix) {
                        "Yytestpod" -> "compilerOpts = -I${buildDir}/cocoapods/synthetic/IOS/Pods/yytestpod/yytestpod/Classes/DebugFramework.framework/Headers -I${buildDir}/cocoapods/synthetic/IOS/Pods/yytestpod/yytestpod/Classes/library/include/DebugLibrary\n"// +
//                                "staticLibraries = libDebugLibrary.a \n" +
//                                "libraryPaths = ${buildDir}/cocoapods/synthetic/IOS/Pods/yytestpod/yytestpod/Classes/library"
                        else -> ""
                    }
                    outputFile.writeText(
                        """
            language = Objective-C
            headers = $headers
            $compilerOpts
            """.trimIndent()
                    )
                }
            }
        }
}