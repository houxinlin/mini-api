package com.hxl.miniapi.core.exception



object ServerException {
    fun create500(msg:String): HttpException {
        return HttpException(msg,500)
    }
}