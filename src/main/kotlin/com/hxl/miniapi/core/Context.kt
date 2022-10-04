package com.hxl.miniapi.core

import com.hxl.miniapi.http.HttpIntercept
import com.hxl.miniapi.http.server.WebServer

/**
 * 应用程序上下文
 */
interface  Context {

    /**
     * 获取参数解析器
     */
    fun getArgumentResolvers():List<ArgumentResolver>

    /**
    * @description: 创建WebServer
    * @date: 2022/10/1 上午10:36
    */

    fun createWebServer(): WebServer


    /**
    * @description: 添加参数转换器
    * @date: 2022/10/1 上午10:37
    */

    fun addArgumentResolvers(vararg argumentResolvers: ArgumentResolver)



    /**
    * @description:  添加结果转换器
    * @date: 2022/10/1 上午10:37
    */

    fun addResultResolvers(vararg resultResolver: ResultResolver)


    fun getResultResolvers():List<ResultResolver>
    /**
    * @description: 刷新上下文
    * @date: 2022/10/1 上午10:37
    */

    fun refresh(start:Class<*>)

    /**
     * 获取RequestMapping
     */
    fun getRequestMappingHandlerMapping(): RequestMappingHandlerMapping


    /**
    * @description: 添加拦截器
    * @date: 2022/10/3 下午8:44
    */
    fun addHttpIntercept(httpIntercept: HttpIntercept)


    /**
    * @description: json转换器
    * @date: 2022/10/3 下午9:20
    */

    fun setJsonConvert(jsonConvert: JsonConvert)

    fun getJsonConvert():JsonConvert
    fun addHttpParameterTypeConverter(vararg httpParameterTypeConverter: HttpParameterTypeConverter<*>)
    fun getHttpParameterTypeConverter():List<HttpParameterTypeConverter<*>>
}