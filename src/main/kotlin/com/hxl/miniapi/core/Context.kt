package com.hxl.miniapi.core

import com.hxl.miniapi.core.auth.MiniAuthentication
import com.hxl.miniapi.http.HttpIntercept
import com.hxl.miniapi.http.server.WebServer
import javax.sql.DataSource

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
    * @description: 获取拦截器
    * @date: 2022/10/5 下午8:20
    */

    fun getHttpIntercept():List<HttpIntercept>

    /**
    * @description: json转换器
    * @date: 2022/10/3 下午9:20
    */

    fun setJsonConvert(jsonConvert: JsonConvert)


    /**
    * @description: 获取JsonConvert
    * @date: 2022/10/5 上午6:28
    */

    fun getJsonConvert():JsonConvert


    /**
    * @description: 添加参数转换String->T
    * @date: 2022/10/5 上午6:28
    */

    fun addHttpParameterTypeConverter(vararg httpParameterTypeConverter: HttpParameterTypeConverter<*>)


    /**
    * @description:  获取参数转换类型
    * @date: 2022/10/5 上午6:29
    */

    fun getHttpParameterTypeConverter():List<HttpParameterTypeConverter<*>>


    /**
    * @description: 设置认证
    * @date: 2022/10/5 上午6:31
    */
    fun getAuthorization():MiniAuthentication?

    /**
    * @description: 设置认证
    * @date: 2022/10/5 下午6:33
    */
    fun setAuthorization(authentication: MiniAuthentication)


    /**
    * @description: 设置DataSource
    * @date: 2022/10/6 上午6:19
    */

    fun setDataSource(dataSource: DataSource)

}