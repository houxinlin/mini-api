package com.hxl.miniapi.test

import com.hxl.miniapi.api.CoolMini
import com.hxl.miniapi.http.MultipartParser

class Main {
}

fun main() {
    val coolMini = CoolMini(7070)
    coolMini.start(Main::class.java)
//    val string="sda\ns".toByteArray()
//
//    MultipartParser(string)

}