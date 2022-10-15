package com.hxl.miniapi.core

import com.hxl.miniapi.http.request.HttpRequest
import com.hxl.miniapi.utils.AntPathMatcher

class RequestMappingHandlerMapping(private val context: Context) {
    private val mappings = mutableListOf<MappingInfo>()
    private  val pathMatcher =AntPathMatcher()

    /**
    * @description: 注册一个mapping
    * @date: 2022/10/1 下午12:47
    */
    fun registerMapping(mappingInfo: MappingInfo){
        mappings.add(mappingInfo)
    }
    fun getHandler(httpRequest: HttpRequest): HandlerMapping?{
        val mappingInfo = mappings.find {
            pathMatcher.match(it.urlPatterns, httpRequest.getRequestPath()) && httpRequest.getHttpMethod()==it.httpMethod
        }
        if (mappingInfo!=null) return HandlerMappingImpl(mappingInfo,context)
        return  null
    }
}