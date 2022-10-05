package com.hxl.miniapi.core.resolver.request

import com.hxl.miniapi.core.ArgumentResolver
import com.hxl.miniapi.core.Context
import com.hxl.miniapi.core.MappingInfo
import com.hxl.miniapi.core.MethodParameter
import com.hxl.miniapi.http.HttpRequestAdapter
import com.hxl.miniapi.utils.isBaseType
import com.hxl.miniapi.utils.isString
import java.lang.reflect.ParameterizedType
import kotlin.reflect.full.isSuperclassOf


/**
* @description: 转换自定义数据类型
* @date: 2022/10/5 上午6:04
*/

class ReferenceArgumentResolver(private val context: Context) :ArgumentResolver {
    override fun support(parameterInfo: MethodParameter, request: HttpRequestAdapter): Boolean {
        return !(parameterInfo.param.type.isString() || parameterInfo.param.type.isBaseType())
    }

    override fun resolver(parameterInfo: MethodParameter, request: HttpRequestAdapter, mappingInfo: MappingInfo): Any? {
        if (List::class.isSuperclassOf(parameterInfo.param.type.kotlin)){
            //泛型参数
            val genericParameterTypes = parameterInfo.method.genericParameterTypes
            //参数位置
            val genericIndex = parameterInfo.method.parameters.indexOfFirst { it.name==parameterInfo.parameterName }
            val parameterizedType =
                genericParameterTypes[genericIndex] as ParameterizedType
            val genericTypeName = parameterizedType.actualTypeArguments[0].typeName
            return context.getJsonConvert().fromJsonList(request.requestBody.decodeToString(),Class.forName(genericTypeName))
        }
        return context.getJsonConvert().fromJson(request.requestBody.decodeToString(),parameterInfo.param.type)
    }
}