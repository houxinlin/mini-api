package com.hxl.miniapi.http

/**
* @description: 拦截器
* @date: 2022/10/3 下午8:42
*/

interface HttpIntercept {

    /**
    * @description: 是否拦截此请求
    * @date: 2022/10/3 下午8:43
    */

    fun intercept(httpAdapter: HttpRequestAdapter):Boolean


    /**
    * @description: 拦截后需要做的事情
    * @date: 2022/10/3 下午8:43
    */

    fun postHandler(httpAdapter: HttpRequestAdapter)
}