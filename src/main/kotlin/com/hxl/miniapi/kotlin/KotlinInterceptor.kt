package com.hxl.miniapi.kotlin

import com.hxl.miniapi.http.HttpIntercept
import com.hxl.miniapi.http.request.HttpRequest
import com.hxl.miniapi.http.response.HttpResponse

class KotlinInterceptor {
    var includePatterns: MutableList<String> = mutableListOf()
    var excludePatterns: MutableList<String> = mutableListOf()

    lateinit var kotlinInterceptorHandlerContent: KotlinInterceptorHandlerContent.() -> Boolean
    fun run(function: KotlinInterceptorHandlerContent.() -> Boolean) {
        this.kotlinInterceptorHandlerContent = function
    }

}

class KotlinInterceptorHandlerContent(val httpRequest: HttpRequest, val httpResponse: HttpResponse) {

}

class KotlinInterceptorWrapper(private val kotlinInterceptorHandlerContent: KotlinInterceptorHandlerContent.() -> Boolean) : HttpIntercept {
    override fun intercept(httpRequest: HttpRequest, httpResponse: HttpResponse): Boolean {
       return kotlinInterceptorHandlerContent.invoke(KotlinInterceptorHandlerContent(httpRequest, httpResponse))
    }

    override fun postHandler(httpRequest: HttpRequest, httpResponse: HttpResponse) {

    }
}