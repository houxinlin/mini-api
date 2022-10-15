package com.hxl.miniapi.core.exception

class ClientException(private val msg:String,  code:Int):HttpException(msg,code) {
}