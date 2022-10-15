package com.hxl.miniapi.http.response

import com.hxl.miniapi.http.cookie.Cookie
import com.sun.net.httpserver.HttpExchange
import java.io.OutputStream

class HttpResponseBase(private val http: HttpExchange, private val userResponse: OutputStream) : HttpResponse {
    private var contentType: String? = null
    private val defaultCharset = "utf-8"
    private var charset = defaultCharset
    private var status = 0
    override fun getOutputStream(): OutputStream {
        return this.userResponse
    }

    override fun addCookie(cookie: Cookie) {
        http.responseHeaders.add("Set-Cookie",cookie.toString())
    }

    override fun addCookie(name: String, value: String) {
        http.responseHeaders.add("Set-Cookie","$name=$value")
    }

    override fun addHeader(key: String, value: String) {
        http.responseHeaders.add(key, value)
    }

    override fun setHeader(key: String, value: String) {
        http.responseHeaders.set(key, value)
    }

    override fun setStatus(code: Int) {
        this.status = code
    }

    override fun sendRedirect(location: String) {
        setHeader("Location",location)
        setStatus(301)
    }

    override fun getStatus(): Int {
        return this.status
    }

    override fun getHeaders(): MutableMap<String, MutableList<String>> {
        return mutableMapOf()
    }
    private fun applyCharset(contentType: String): String {
        if (contentType.lowercase().indexOf("charset=") != -1) return contentType
        if (contentType.endsWith(";")) return "$contentType ;charset=${getEncode()}"
        return "$contentType; charset=${getEncode()}"
    }

    override fun setContentType(contentType: String) {
        this.contentType = applyCharset(contentType)
    }

    override fun getContentType(): String? {
        return this.contentType
    }

    override fun setEncode(encode: String) {
        this.charset = encode
    }

    override fun getEncode(): String {
        return this.charset
    }
}