import java.io.BufferedReader
import java.io.InputStreamReader

object SimulatorHelp {
    fun getShellResult(shell:String):String{
        val process = shell.execute()
        val result = process.text()
        println("SHELL_EXEC===Result:$result")
        return result
    }

    fun getDeviceNameAndId():Pair<String,String>{
        val runtimesShell = "xcrun simctl list runtimes --json".execute()
        val runtimesJsonStr = runtimesShell.text()
        val runtimesJson = groovy.json.JsonSlurper().parseText(runtimesJsonStr) as Map<String, Any>
        val firstid = ((runtimesJson["runtimes"] as ArrayList<Any>).filter {
            (it as Map<String,Any>)["platform"]?.equals("iOS") == true
        }.first() as Map<String,Any>)["identifier"]
        println("执行测试的模拟器===identifier:$firstid")
        val devicesShell = "xcrun simctl list devices --json".execute()
        val devicesJsonStr = devicesShell.text()
        val devicesJson = groovy.json.JsonSlurper().parseText(devicesJsonStr) as Map<String, Any>
        val devices = devicesJson["devices"] as Map<String, Any>
        var name = ""
        var deviceId = ""
        val runtimedevices = devices.get(firstid) as List<Any>

        for (i in runtimedevices.lastIndex downTo 0) {
            val a = runtimedevices[i] as Map<String,Any>
            val isAvailable = a["isAvailable"] as Boolean
            val state = a["state"] as String
            val tempName = a["name"] as String
            if(isAvailable && state == "Shutdown" && tempName.contains("iPhone")){
                name = tempName
                deviceId = a["udid"] as String
                break
            }
        }
        println("执行测试的模拟器===UDID:$deviceId")
        println("执行测试的模拟器===Name:$name")
        return Pair(name,deviceId)
    }
}

/**
 * 给String扩展 execute() 函数
 */
fun String.execute(): Process {
    val runtime = Runtime.getRuntime()
    return runtime.exec(this)
}

/**
 * 扩展Process扩展 text() 函数
 */
fun Process.text(): String {
    // 输出 Shell 执行结果
    val inputStream = this.inputStream
    val insReader = InputStreamReader(inputStream)
    val bufReader = BufferedReader(insReader)

    var output = ""
    var line: String? =""
    while (null!=line) {
        // 逐行读取shell输出，并保存到变量output
        line = bufReader.readLine()
        output += line +"\n"
    }
    return output
}
