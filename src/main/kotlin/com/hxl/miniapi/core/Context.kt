package com.hxl.miniapi.core

import com.google.gson.Gson
import com.hxl.miniapi.http.HttpIntercept
import com.hxl.miniapi.http.InterceptorRegistration
import com.hxl.miniapi.http.response.ClientErrorPageResponse
import com.hxl.miniapi.http.response.ServerErrorPageResponse
import com.hxl.miniapi.http.server.WebServer
import com.hxl.miniapi.kotlin.MiniKotlinApi
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

    fun addArgumentResolvers(first: Boolean=false,vararg argumentResolvers: ArgumentResolver)



    /**
    * @description:  添加结果转换器
    * @date: 2022/10/1 上午10:37
    */

    fun addResultResolvers(first: Boolean=false,vararg resultResolver: ResultResolver)


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
    fun addHttpIntercept(httpIntercept: HttpIntercept): InterceptorRegistration


    /**
    * @description: 获取拦截器
    * @date: 2022/10/5 下午8:20
    */

    fun getHttpIntercept():List<InterceptorRegistration>

    /**
    * @description: json转换器
    * @date: 2022/10/3 下午9:20
    */

    fun setJsonConvert(jsonConvert: JsonConvert)


    /**
    * @description: 获取JsonConvert
    * @date: 2022/10/5 上午6:28
    */

    fun getJsonConvert():JsonConvert?


    /**
    * @description: 添加参数转换String->T
    * @date: 2022/10/5 上午6:28
    */

    fun addHttpParameterTypeConverter(first: Boolean=false,vararg httpParameterTypeConverter: HttpParameterTypeConverter<*>)


    /**
    * @description:  获取参数转换类型
    * @date: 2022/10/5 上午6:29
    */

    fun getHttpParameterTypeConverter():List<HttpParameterTypeConverter<*>>

    /**
    * @description: 设置DataSource
    * @date: 2022/10/6 上午6:19
    */

    fun setDataSource(dataSource: DataSource)

    /**
     * session管理器
     */
    fun getManager():Manager

    /**
     * 客户端请求错误回复模板
     */
    fun setClientErrorPageResponse(clientErrorPageResponse: ClientErrorPageResponse)

    fun getClientErrorPageResponse():ClientErrorPageResponse

    /**
     * 服务器错误回复模板
     */
    fun setServerErrorPageResponse(serverErrorPageResponse: ServerErrorPageResponse)

    fun getServerErrorPageResponse():ServerErrorPageResponse

    /**
     * 注册Controller
     */
    fun registerController(vararg controllerClass: Class<*>)


    fun setGson(gson: Gson)

    fun getGson():Gson?
    fun whithKotlin(function: MiniKotlinApi.() -> Unit)
}