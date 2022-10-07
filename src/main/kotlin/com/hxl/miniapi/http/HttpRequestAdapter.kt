package com.hxl.miniapi.http

import com.hxl.miniapi.http.session.Session
import com.hxl.miniapi.http.session.SessionManager
import com.hxl.miniapi.utils.urlArgumentToMap
import com.sun.net.httpserver.HttpExchange
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

open class HttpRequestAdapter(private val httpExchange: HttpExchange) {
    companion object {
        const val SESSION_ID = "MINI_API_SESSION"
    }

    val requestBody = httpExchange.requestBody.buffered().readBytes()

    private var responseMessage: ResponseMessage? = null

    fun getRequestPath(): String {
        return httpExchange.requestURI.path.toString().removeSuffix("/")
    }

    fun getQueryPath(): String {
        val requestURI = httpExchange.requestURI
        return if (requestURI.query==null) "" else requestURI.query
    }

    fun getRequestMethod(): HttpMethod {
        return when (httpExchange.requestMethod) {
            "GET" -> HttpMethod.GET
            "POST" -> HttpMethod.POST
            "DELETE" -> HttpMethod.DELETE
            "PUT" -> HttpMethod.PUT
            else -> HttpMethod.GET
        }
    }

    fun getParameter(parameterName: String): String? {
        if (httpExchange.requestMethod == HttpMethod.GET.toString()) {
            return parameterName.urlArgumentToMap().getOrDefault(parameterName, null)
        }
        if (httpExchange.requestMethod != HttpMethod.GET.toString() &&
            getContentType() == ContentType.WWW_FORM_URLENCODEED
        ) {
            return requestBody.decodeToString().urlArgumentToMap().getOrDefault(parameterName, null)
        }
        return null
    }

    fun getStringContentType(): String? {
        var keys = httpExchange.requestHeaders.keys
        val contentType = httpExchange.requestHeaders.keys.find { it.equals("Content-type", ignoreCase = false) }
            ?: return null
        return httpExchange.requestHeaders.getFirst(contentType)
    }

    fun getContentType(): ContentType? {
        val contentType = getStringContentType() ?: return null
        if (contentType.equals(ContentType.TEXT_PLAIN.contentType, ignoreCase = false)) return ContentType.TEXT_PLAIN
        if (contentType.equals(
                ContentType.APPLICATION_JSON.contentType,
                ignoreCase = false
            )
        ) return ContentType.APPLICATION_JSON
        if (contentType.equals(
                ContentType.WWW_FORM_URLENCODEED.contentType,
                ignoreCase = false
            )
        ) return ContentType.WWW_FORM_URLENCODEED
        if (contentType.equals(ContentType.FORM_DATA.contentType, ignoreCase = false)) return ContentType.FORM_DATA
        return null
    }

    private fun getSessionId(): String? {
        val cookie = httpExchange.requestHeaders.getFirst("Cookie") ?: return null
        val matcher = Pattern.compile("(.*?)=(.*?)(\$|;|,(?! ))").matcher(cookie)
        while (matcher.find()) {
            if (matcher.group(1) == SESSION_ID) return matcher.group(2)
        }
        return null
    }

    fun getSession(): Session {
        //如果当前客户端存在sessionId，从SessionManager中获取
        var sessionId = getSessionId() //Cookie存在SessionID
        if (!sessionId.isNullOrBlank()) {
            val session = SessionManager.getSession(sessionId)
            if (session != null) return session
        }
        //没有sessionId情况
        sessionId = SessionManager.newSession().getSessionId()
        setResponseCookie(httpExchange, sessionId)
        return SessionManager.getSession(sessionId)!!
    }

    private fun setResponseCookie(httpExchange: HttpExchange, sessionId: String) {
        val maxAge =TimeUnit.DAYS.toMillis(1)
        httpExchange.responseHeaders.set("Set-Cookie", "$SESSION_ID=$sessionId;Path=/ ;max-age=$maxAge")
    }


    /**
     * @description: 设置响应内容
     * @date: 2022/10/5 下午7:02
     */

    fun setResponse(data: Any, header: Map<String, String> = mutableMapOf(), code: Int = 200) {
        this.responseMessage = ResponseMessage(header, BeanHttpResponse(code, data))
    }

    fun setResponse(data: Any, header: Map<String, String> = mutableMapOf()) {
        this.responseMessage = ResponseMessage(header, BeanHttpResponse(200, data))
    }

    fun setResponse(data: Any) {
        this.responseMessage = ResponseMessage(mutableMapOf(), BeanHttpResponse(200, data))
    }

    fun getResponse(): ResponseMessage? {
        return this.responseMessage
    }


}