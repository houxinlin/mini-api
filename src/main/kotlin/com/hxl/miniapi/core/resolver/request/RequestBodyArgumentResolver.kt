package com.hxl.miniapi.core.resolver.request

import com.hxl.miniapi.core.ArgumentResolver
import com.hxl.miniapi.core.Context
import com.hxl.miniapi.core.MappingInfo
import com.hxl.miniapi.core.MethodParameter
import com.hxl.miniapi.core.convert.SimpleTypeConverter
import com.hxl.miniapi.http.HttpMethod
import com.hxl.miniapi.http.anno.param.RequestBody
import com.hxl.miniapi.http.request.HttpRequest
import com.hxl.miniapi.http.response.HttpResponse
import com.hxl.miniapi.utils.isBaseType
import com.hxl.miniapi.utils.isString
import java.lang.reflect.ParameterizedType
import kotlin.reflect.full.isSuperclassOf

class RequestBodyArgumentResolver(private val context: Context):ArgumentResolver {
    private val simpleTypeArgumentResolver  =SimpleTypeConverter()
    override fun support(parameterInfo: MethodParameter, request: HttpRequest): Boolean {
        return request.getHttpMethod()!= HttpMethod.GET &&
                parameterInfo.hasAnnotation(RequestBody::class.java)
    }

    override fun resolver(
        parameterInfo: MethodParameter,
        request: HttpRequest,
        response: HttpResponse,
        mappingInfo: MappingInfo
    ): Any? {

        if (parameterInfo.param.type.isString()) return request.getBodyInputStream().readBytes().decodeToString()
//        //基本数据类型转换
        if (parameterInfo.param.type.isBaseType())
            return simpleTypeArgumentResolver.typeConvert(parameterInfo.param.type,request.getBodyInputStream().readBytes().decodeToString())
//        //如果是Lits类型
        if (List::class.isSuperclassOf(parameterInfo.param.type.kotlin)){
            //泛型参数
            val genericParameterTypes = parameterInfo.method.genericParameterTypes
            //参数位置
            val genericIndex = parameterInfo.method.parameters.indexOfFirst { it.name==parameterInfo.parameterName }
            val parameterizedType =
                genericParameterTypes[genericIndex] as ParameterizedType
            val genericTypeName = parameterizedType.actualTypeArguments[0].typeName
            return context.getJsonConvert()!!.fromJsonList(request.getBodyInputStream().readBytes().decodeToString(),Class.forName(genericTypeName))
        }
        return context.getJsonConvert()!!.fromJson(request.getBodyInputStream().readBytes().decodeToString(),parameterInfo.param.type)

    }
}