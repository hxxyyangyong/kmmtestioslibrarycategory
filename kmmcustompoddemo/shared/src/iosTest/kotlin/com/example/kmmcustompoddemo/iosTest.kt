package com.example.kmmcustompoddemo

import kotlin.test.Test
import kotlin.test.assertTrue
import cocoapods.yytestpod.TTDemo
import cocoapods.yytestpod.DebugLibrary

class IosGreetingTest {

    @Test
    fun testExample() {
        assertTrue(Greeting().greeting().contains("iOS"), "Check iOS is mentioned")
    }

    @Test
    fun calctTwoDate() {
        println("Test:"+TTDemo.callTTDemoCategoryMethod())
        println("Test2:"+TTDemo.callNSStrigCategoryMethod())
        println("Test3:"+DebugLibrary.debugCategoryMethod())
        assertTrue(TTDemo.callTTDemoCategoryMethod() == "TTDemo+kmm categoryMethod","Call TTDemo Categote Method Error")
        assertTrue(TTDemo.callNSStrigCategoryMethod() == "NSString+kmm","Call NSString Categote Method Error")
    }


}