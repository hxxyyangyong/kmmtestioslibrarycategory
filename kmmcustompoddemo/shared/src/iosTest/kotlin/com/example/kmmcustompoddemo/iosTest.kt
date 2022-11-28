package com.example.kmmcustompoddemo

import kotlin.test.Test
import kotlin.test.assertTrue
import cocoapods.yytestpod.TTDemo
import cocoapods.yytestpod.DebugLibrary
import cocoapods.yytestpod.libraryStringCategory
import cocoapods.yytestpod.categoryMethod
import cocoapods.yytestpod.kmmString
import platform.Foundation.NSString

class IosGreetingTest {

    @Test
    fun testExample() {
        assertTrue(Greeting().greeting().contains("iOS"), "Check iOS is mentioned")
    }

    @Test
    fun calctTwoDate() {
        println("Test1:"+TTDemo.callTTDemoCategoryMethod())//It is OK for the class in the framework to indirectly call the category method of its own class
        println("Test2:"+TTDemo.callNSStrigCategoryMethod())//It is OK for a class in the framework to indirectly call the category method of other classes
        println("Test3:"+TTDemo.categoryMethod())//It is OK for a class in the framework to Directly call the category method of of its own class
        println("Test4:"+NSString.kmmString())//It is OK for a class in the framework to Directly call the category method of other classes
        //-----
        println("Test5"+ DebugLibrary.debugCategoryMethod())//❌The class in .a indirectly calls the category method of other classes and throws an exception
        println("Test6"+ NSString.libraryStringCategory())//❌Directly calling the category method of the class in .a also throws an exception
        //----
        assertTrue(TTDemo.callTTDemoCategoryMethod() == "TTDemo+kmm categoryMethod","Framework Call TTDemo Category Method Error")
        assertTrue(TTDemo.callNSStrigCategoryMethod() == "NSString+kmm","Framework Call NSString Category Method Error")
//        assertTrue(DebugLibrary.debugCategoryMethod() == "libraryStringCategory","Library DebugLibrary Class Call NSString Category Method Error")
//        assertTrue(NSString.libraryStringCategory() == "libraryStringCategory","Call Library NSString Class Category Method Error")
    }


}