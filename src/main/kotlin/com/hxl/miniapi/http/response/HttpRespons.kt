package com.hxl.miniapi.http.response

import com.hxl.miniapi.http.cookie.Cookie
import java.io.OutputStream


/**
* @description: http响应抽象
* @date: 2022/10/10 上午3:54
*/

interface HttpRespons {
    fun getOutputStream():OutputStream

    fun setCookie(cookie: Cookie)

    fun setHeader(key:String,value:String)

    fun setStatus(code:Int)

    fun sendRedirect(location:String)
}