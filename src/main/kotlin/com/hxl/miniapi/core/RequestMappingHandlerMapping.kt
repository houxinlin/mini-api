package com.hxl.miniapi.core

import com.hxl.miniapi.http.HttpRequestAdapter
import com.hxl.miniapi.utils.AntPathMatcher
import com.sun.net.httpserver.HttpExchange

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

    /**
    * @description: 移除一个mapping
    * @date: 2022/10/1 下午12:48
    */
    fun removeMapping(mappingInfo: MappingInfo){

    }
    fun getHandler(httpRequestAdapter: HttpRequestAdapter): HandlerMapping?{
        val mappingInfo = mappings.find {
            pathMatcher.match(it.urlPatterns, httpRequestAdapter.getRequestPath()) && httpRequestAdapter.getRequestMethod()==it.httpMethod
        }
        if (mappingInfo!=null) return HandlerMappingImpl(mappingInfo,context)
        return  null
    }
}