package com.hxl.miniapi.core

import com.hxl.miniapi.http.HttpMethod
import java.lang.reflect.Method

class MappingInfo {

    /**
     * @description: 对象实例
     * @date: 2022/10/1 下午12:57
     */

    lateinit var instance: Any


    /**
     * @description: url路径
     * @date: 2022/10/1 下午12:59
     */

    lateinit var urlPatterns: String


    /**
     * @description: 处理方法
     * @date: 2022/10/1 下午12:59
     */

    lateinit var method: Method


    /**
     * @description: http请求方法
     * @date: 2022/10/1 下午1:14
     */
    var httpMethod: HttpMethod = HttpMethod.GET
}