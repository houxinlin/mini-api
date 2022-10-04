package com.hxl.miniapi.core.exception

class HttpException(private val msg:String, val code:Int):Exception(msg)  {
}