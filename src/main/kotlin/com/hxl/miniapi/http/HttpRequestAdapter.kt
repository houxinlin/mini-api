package com.hxl.miniapi.http

import com.hxl.miniapi.utils.urlArgumentToMap
import com.sun.net.httpserver.HttpExchange

open class HttpRequestAdapter(private val httpExchange: HttpExchange) {
    val requestBody = httpExchange.requestBody.buffered().readBytes()

    fun getRequestPath(): String {
        return httpExchange.requestURI.toString().removeSuffix("/")
    }

    fun getQueryPath(): String {
        return httpExchange.requestURI.query
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
        val contentType = httpExchange.requestHeaders.keys.find { it.equals("Content-Type", ignoreCase = false) }
            ?: return null
        return httpExchange.requestHeaders.getFirst(contentType)
    }

    fun getContentType(): ContentType? {
        val contentType = getStringContentType() ?: return null
        if (contentType.contains(ContentType.TEXT_PLAIN.contentType)) return ContentType.TEXT_PLAIN
        if (contentType.contains(ContentType.APPLICATION_JSON.contentType)) return ContentType.APPLICATION_JSON
        if (contentType.contains(ContentType.WWW_FORM_URLENCODEED.contentType)) return ContentType.WWW_FORM_URLENCODEED
        if (contentType.contains(ContentType.FORM_DATA.contentType)) return ContentType.FORM_DATA
        return null
    }

}