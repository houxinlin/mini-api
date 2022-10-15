package com.hxl.miniapi.core.exception

class ServerException(private val msg:String,  code:Int):HttpException(msg,code) {}