package com.hxl.miniapi.core

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
    fun getHandler(path:String,httpExchange: HttpExchange): HandlerMapping?{
        val mappingInfo = mappings.find {
            pathMatcher.match(it.urlPatterns, path) && httpExchange.requestMethod==it.httpMethod.toString()
        }
        if (mappingInfo!=null) return HandlerMappingImpl(mappingInfo,context)
        return  null
    }
}