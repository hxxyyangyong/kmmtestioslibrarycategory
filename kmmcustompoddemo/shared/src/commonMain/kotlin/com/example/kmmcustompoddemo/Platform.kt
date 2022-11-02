package com.example.kmmcustompoddemo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform