package com.hxl.miniapi.http.response

import com.hxl.miniapi.http.cookie.Cookie
import java.io.OutputStream


/**
* @description: http响应抽象
* @date: 2022/10/10 上午3:54
*/

interface HttpResponse {
    fun getOutputStream():OutputStream
    fun addCookie(cookie: Cookie)
    fun addCookie(name:String,value:String)
    fun setHeader(key:String,value:String)
    fun addHeader(key:String,value:String)
    fun setStatus(code:Int)
    fun sendRedirect(location:String)
    fun getStatus():Int
    fun getHeaders():MutableMap<String,MutableList<String>>
    fun setContentType(contentType:String)
    fun getContentType():String?
    fun  setEncode(encode:String)
    fun getEncode():String
}