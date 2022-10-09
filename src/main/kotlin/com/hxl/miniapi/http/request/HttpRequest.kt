package com.hxl.miniapi.http.request

import com.hxl.miniapi.http.cookie.Cookie
import com.mysql.cj.xdevapi.Session
import java.io.InputStream

/**
* @description: http请求抽象
* @date: 2022/10/10 上午3:54
*/

interface HttpRequest {
    fun getCookie():Cookie

    fun getSession():Session

    fun getParameter(key:String)

    fun getParameterKeys():List<String>

    fun getBodyInputStream():InputStream
}