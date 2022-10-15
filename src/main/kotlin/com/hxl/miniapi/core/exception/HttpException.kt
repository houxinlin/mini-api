package com.hxl.miniapi.core.exception

open class HttpException(private val msg:String, val code:Int):Exception(msg)  {
}