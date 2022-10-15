package com.hxl.miniapi.http.request

import com.hxl.miniapi.core.Context
import com.hxl.miniapi.core.Manager
import com.hxl.miniapi.http.ContentType
import com.hxl.miniapi.http.HttpMethod
import com.hxl.miniapi.http.cookie.Cookie
import com.hxl.miniapi.http.file.FilePart
import com.hxl.miniapi.http.session.Session
import com.sun.net.httpserver.HttpExchange
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.regex.Pattern

class HttpRequestBase(val http: HttpExchange) : HttpRequest {
    private lateinit var context: Context
    private val body: ByteArray = http.requestBody.readBytes()
    private val parameter = Parameter(this)

    override fun setContext(context: Context) {
        this.context = context
    }

    override fun getContext(): Context {
        return context
    }

    override fun getCookies(): Array<Cookie> {
        val cookieStr = http.requestHeaders.getFirst("Cookie")
        if (cookieStr.isNullOrEmpty()) return arrayOf()
        val matcher = Pattern.compile("(.*?)=(.*?)(\$|;|,(?! ))").matcher(cookieStr)
        val cookies = mutableListOf<Cookie>()
        while (matcher.find()) {
            cookies.add(Cookie(matcher.group(1), matcher.group(2)))
        }
        return cookies.toTypedArray()
    }

    private fun createNewSession():Session{
        val session = context.getManager().createNewSession()
        //返回响应
        http.responseHeaders.add("Set-Cookie", "${Manager.SESSION_ID}=${session.getSessionId()}")
        return session
    }
    override fun getSession(): com.hxl.miniapi.http.session.Session {
        //如果没有sessionID，则创建新的
        val sessionId = getSessionId() ?: return createNewSession()

        //如果服务器没有此sessionId信息，则创建新的
        return context.getManager().findSession(sessionId) ?: return createNewSession()
    }

    override fun getParameter(key: String): String? {
        return parameter.getParamter(key)
    }

    override fun getParameterKeys(): Set<String> {
        return parameter.getParamterKeys()
    }

    override fun getBodyInputStream(): InputStream {
        return ByteArrayInputStream(body)
    }

    override fun getHttpMethod(): HttpMethod {
        return when (http.requestMethod) {
            "GET" -> HttpMethod.GET
            "POST" -> HttpMethod.POST
            "DELETE" -> HttpMethod.DELETE
            "PUT" -> HttpMethod.PUT
            else -> HttpMethod.GET
        }
    }

    override fun getUrl(): String {
        return http.requestURI.toString()
    }

    override fun getRequestPath(): String {
        return http.requestURI.path.toString().removeSuffix("/")
    }

    override fun getFile(name: String): FilePart? {
        return parameter.getFile(name)
    }

    override fun listFile(): List<FilePart> {
        return parameter.listFile()
    }

    override fun getQueryString(): String? {
        return http.requestURI.query
    }

    override fun getContentType(): String {
        return http.requestHeaders.getFirst("Content-type") ?: return ContentType.TEXT_PLAIN.contentType
    }

    private fun getSessionId(): String? {
        val cookie = getCookies().find { it.name == Manager.SESSION_ID } ?: return null
        return cookie.value
    }
}