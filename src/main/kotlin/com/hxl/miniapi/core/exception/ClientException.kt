package com.hxl.miniapi.core.exception

import com.hxl.miniapi.http.HttpStatus

object ClientException{
    fun create400(msg:String):HttpException{
        return HttpException(msg,HttpStatus.CLIENT_ERROR.code)
    }
    fun create404(msg:String):HttpException{
        return HttpException(msg,HttpStatus.NOT_FOUND.code)
    }
    fun create403(msg:String):HttpException{
        return HttpException(msg,HttpStatus.NOT_FOUND.code)
    }
}