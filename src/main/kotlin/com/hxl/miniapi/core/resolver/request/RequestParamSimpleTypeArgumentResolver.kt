package com.hxl.miniapi.core.resolver.request

import com.hxl.miniapi.core.MethodParameter
import com.hxl.miniapi.core.ArgumentResolver
import com.hxl.miniapi.core.Context
import com.hxl.miniapi.core.MappingInfo
import com.hxl.miniapi.core.convert.SimpleTypeConverter
import com.hxl.miniapi.core.exception.ClientException
import com.hxl.miniapi.core.exception.ServerException
import com.hxl.miniapi.http.*
import com.hxl.miniapi.http.anno.param.RequestParam
import com.hxl.miniapi.utils.isBaseType
import com.hxl.miniapi.utils.isString
import com.hxl.miniapi.utils.urlArgumentToMap

/**
 * @description: 支持参数到基础数据类型+String类型的转换
 * @date: 2022/10/4 上午6:06
 */

class RequestParamSimpleTypeArgumentResolver(private val context: Context) : ArgumentResolver {
    private val simpleTypeConverter = SimpleTypeConverter()
    override fun support(parameterInfo: MethodParameter, request: HttpRequestAdapter): Boolean {
        return parameterInfo.hasAnnotation (RequestParam::class.java) ||   (parameterInfo.param.type.isBaseType() || parameterInfo.param.type.isString())
    }

    override fun resolver(parameterInfo: MethodParameter, request: HttpRequestAdapter, mappingInfo: MappingInfo): Any? {
        //获取要取得的参数名称
        val argumentName = if (parameterInfo.hasAnnotation(RequestParam::class.java)) {
            parameterInfo.getAnnotation(RequestParam::class.java)!!.value
        } else parameterInfo.parameterName
        //get请求，从url中获取参数
        if (request.getRequestMethod() == HttpMethod.GET) {
            return getArgumentFromUrl(request.getQueryPath(), argumentName, parameterInfo, request)
        }
        //post、put、delete、如果是x-www-form-urlencoded请求，从body中获取参数
        if ((request.getRequestMethod() != HttpMethod.GET) &&
            request.getContentType() == ContentType.WWW_FORM_URLENCODEED
        ) {
            val requestBody = request.requestBody.decodeToString()
            return getArgumentFromUrl(requestBody, argumentName, parameterInfo, request)
        }
        if (request.getRequestMethod() != HttpMethod.GET && request is HttpMultipartAdapter) {
            val propertys = MultipartParser(request.requestBody, request.getBoundary()).getPropertys()
            if (propertys[argumentName] == null) throw ClientException.create400("${argumentName}不存在")
            return propertys[argumentName]
        }

        throw IllegalArgumentException("无法解析参数")
    }

    //
    /**
     * @description: 从查询参数中获取目标参数创建对象并转换
     * @date: 2022/10/3 下午10:56
     */

    private fun getArgumentFromUrl(
        urlQuery: String,
        argumentName: String,
        methodParameter: MethodParameter,
        request: HttpRequestAdapter
    ): Any? {
        val requireClassType = methodParameter.param.type
        val argumentValue = urlQuery.urlArgumentToMap()[argumentName]
            ?: throw ClientException.create400("参数${argumentName}不存在")
        //如果参数是String类型，则直接返回
        if (requireClassType.isString()) return argumentValue
        //如果参数是基本数据类型，则转换
        if (requireClassType.isBaseType()) {
            return simpleTypeConverter.typeConvert(requireClassType, argumentValue)
        }

        val httpParameterTypeConverter =
            context.getHttpParameterTypeConverter().find { it.canConvert(methodParameter, argumentValue) }
                ?: throw ServerException.create500("找不到参数转换器$argumentName")
        return httpParameterTypeConverter.typeConvert(argumentValue)
    }

}