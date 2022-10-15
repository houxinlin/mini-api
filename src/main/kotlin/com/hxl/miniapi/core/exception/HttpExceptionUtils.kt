package com.hxl.miniapi.core.exception

import com.hxl.miniapi.http.HttpStatus

object HttpExceptionUtils{
    fun create400(msg:String):HttpException{
        return ClientException(msg,HttpStatus.CLIENT_ERROR.code)
    }
    fun create404(msg:String):HttpException{
        return ClientException(msg,HttpStatus.NOT_FOUND.code)
    }
    fun create403(msg:String):HttpException{
        return ClientException(msg,HttpStatus.NOT_FOUND.code)
    }

    fun create500(msg:String): HttpException {
        return ServerException(msg,500)
    }
}