package com.example.hellokmm

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform