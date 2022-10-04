package com.hxl.miniapi.core.exception

object ClientException{
    fun create400(msg:String):HttpException{
        return HttpException(msg,400)
    }
    fun create404(msg:String):HttpException{
        return HttpException(msg,404)
    }
}