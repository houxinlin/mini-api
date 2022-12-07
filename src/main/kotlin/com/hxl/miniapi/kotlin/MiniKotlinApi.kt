package com.hxl.miniapi.kotlin

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hxl.miniapi.core.MappingInfo
import com.hxl.miniapi.core.MiniContext
import com.hxl.miniapi.http.HttpMethod
import com.hxl.miniapi.http.request.HttpRequest
import com.hxl.miniapi.http.response.HttpResponse
import com.hxl.miniapi.orm.mybatis.MysqlDataSource
import com.hxl.miniapi.utils.addPrefixIfMiss

class MiniKotlinApi(private val miniContext: MiniContext) {
    private fun createMappingInfo(
        url: String,
        kotlinRequestApi: KotlinRequestApi.() -> Unit,
        httpMethod: HttpMethod
    ): MappingInfo {
        return MappingInfo().apply {
            this.instance = MappingInstanceWrapper(miniContext, kotlinRequestApi)
            this.httpMethod = httpMethod
            this.method = this.instance::class.java.getDeclaredMethod(
                "handler",
                HttpRequest::class.java,
                HttpResponse::class.java
            )
            this.urlPatterns = url.addPrefixIfMiss("/")
        }
    }

    fun get(url: String, kotlinRequestApi: KotlinRequestApi.() -> Unit) {
        miniContext.getRequestMappingHandlerMapping()
            .registerMapping(createMappingInfo(url, kotlinRequestApi, HttpMethod.GET))
    }

    fun post(url: String, kotlinRequestApi: KotlinRequestApi.() -> Unit) {
        miniContext.getRequestMappingHandlerMapping()
            .registerMapping(createMappingInfo(url, kotlinRequestApi, HttpMethod.POST))
    }

    fun delete(url: String, kotlinRequestApi: KotlinRequestApi.() -> Unit) {
        miniContext.getRequestMappingHandlerMapping()
            .registerMapping(createMappingInfo(url, kotlinRequestApi, HttpMethod.DELETE))
    }

    fun put(url: String, kotlinRequestApi: KotlinRequestApi.() -> Unit) {
        miniContext.getRequestMappingHandlerMapping()
            .registerMapping(createMappingInfo(url, kotlinRequestApi, HttpMethod.PUT))
    }

    fun configDatabase(function: Database.() -> Unit) {
        val database = Database()
        function.invoke(database)
        miniContext.setDataSource(MysqlDataSource(database.userName, database.password, database.url))
    }

    fun interceptor(function: KotlinInterceptor.() -> Unit) {
        val kotlinInterceptor = KotlinInterceptor()
        function.invoke(kotlinInterceptor)
        miniContext.addHttpIntercept(KotlinInterceptorWrapper(kotlinInterceptor.kotlinInterceptorHandlerContent))
            .includePathPatterns(kotlinInterceptor.includePatterns)
            .excludePathPatterns(kotlinInterceptor.excludePatterns)
    }

    fun gson(function: GsonBuilder.() -> Unit){
        val gsonBuilder = GsonBuilder()
        function.invoke(gsonBuilder)
        miniContext.setGson(gsonBuilder.create())
    }
}