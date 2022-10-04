package com.hxl.miniapi.core.resolver.request

import com.hxl.miniapi.core.MethodParameter
import com.hxl.miniapi.core.ArgumentResolver
import com.hxl.miniapi.core.MappingInfo
import com.hxl.miniapi.http.HttpRequestAdapter
import com.hxl.miniapi.http.anno.param.RequestUri
import com.hxl.miniapi.utils.getDefaultValue
import com.hxl.miniapi.utils.isBaseType
import com.hxl.miniapi.utils.isString


/**
 * @description: 转换请求地址@RequestUri
 * @date: 2022/10/5 上午6:05
 */

class RequestUriArgumentResolver : ArgumentResolver {
    override fun support(parameterInfo: MethodParameter, request: HttpRequestAdapter): Boolean {
        return parameterInfo.hasAnnotation(RequestUri::class.java) && parameterInfo.param.type.isString()
    }

    override fun resolver(parameterInfo: MethodParameter, request: HttpRequestAdapter, mappingInfo: MappingInfo): Any? {
        return request.getRequestPath()
    }
}