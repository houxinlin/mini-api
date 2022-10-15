package com.hxl.miniapi.core.resolver.request

import com.hxl.miniapi.core.ArgumentResolver
import com.hxl.miniapi.core.Context
import com.hxl.miniapi.core.MappingInfo
import com.hxl.miniapi.core.MethodParameter
import com.hxl.miniapi.core.convert.SimpleTypeConverter
import com.hxl.miniapi.core.exception.HttpExceptionUtils
import com.hxl.miniapi.http.anno.param.RequestParam
import com.hxl.miniapi.http.cookie.Cookie
import com.hxl.miniapi.http.file.FilePart
import com.hxl.miniapi.http.request.HttpRequest
import com.hxl.miniapi.http.response.HttpResponse
import com.hxl.miniapi.http.session.Session
import com.hxl.miniapi.utils.isString

/**
 * @description: 支持参数到基础数据类型+String类型的转换
 * @date: 2022/10/4 上午6:06
 */

class RequestParamSimpleTypeArgumentResolver(private val context: Context) : ArgumentResolver {
    private val simpleTypeConverter = SimpleTypeConverter()
    override fun support(parameterInfo: MethodParameter, request: HttpRequest): Boolean {
        return parameterInfo.hasAnnotation(RequestParam::class.java) &&
                (parameterInfo.param.type != Session::class.java ||
                        parameterInfo.param.type != Cookie::class.java ||
                        parameterInfo.param.type != FilePart::class.java)
    }

    override fun resolver(
        parameterInfo: MethodParameter,
        request: HttpRequest,
        response: HttpResponse,
        mappingInfo: MappingInfo
    ): Any? {
        //获取要取得的参数名称
        val argumentName = if (parameterInfo.hasAnnotation(RequestParam::class.java)) {
            parameterInfo.getAnnotation(RequestParam::class.java)!!.value
        } else parameterInfo.parameterName

        val parameter: String = request.getParameter(argumentName) ?: throw HttpExceptionUtils.create400("参数找不到")
        //如果是字符类型，则直接返回
        if (parameterInfo.param.type.isString()) return parameter
        //如果是基础数据类型
        if (simpleTypeConverter.canConvert(parameterInfo.param.type, parameter)) {
            return simpleTypeConverter.typeConvert(parameterInfo.param.type, parameter)
        }
        val typeConverter = context.getHttpParameterTypeConverter().find { it.canConvert(parameterInfo, parameter) }
                ?: throw HttpExceptionUtils.create500("参数无法转换")
        return typeConverter.typeConvert(parameter)

    }

}