package com.hxl.miniapi.http.request

import com.hxl.miniapi.core.Context
import com.hxl.miniapi.http.HttpMethod
import com.hxl.miniapi.http.cookie.Cookie
import com.hxl.miniapi.http.file.FilePart
import com.hxl.miniapi.http.session.Session
import java.io.InputStream

/**
* @description: http请求抽象
* @date: 2022/10/10 上午3:54
*/

interface HttpRequest {
    fun getCookies():Array<Cookie>

    fun getSession():Session

    fun getParameter(key:String):String?

    fun getParameterKeys():Set<String>

    fun getBodyInputStream():InputStream

    fun getHttpMethod():HttpMethod

    fun getUrl():String

    fun getQueryString():String?

    fun getRequestPath():String

    fun getFile(name:String):FilePart?

    fun listFile():List<FilePart>

    fun getContentType():String

    fun setContext(context:Context)

    fun getContext():Context
}