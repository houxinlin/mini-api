package com.hxl.miniapi.core.convert

import com.hxl.miniapi.core.HttpParameterTypeConverter
import com.hxl.miniapi.core.MethodParameter
import com.hxl.miniapi.http.HttpRequestAdapter
import java.time.LocalDateTime

class HttpParameteLocalDateTimeTypeConverter: HttpParameterTypeConverter<LocalDateTime> {
    override fun canConvert(methodParameter: MethodParameter, request: HttpRequestAdapter): Boolean {
        return methodParameter.param.type ==LocalDateTime::class.java
    }

    override fun typeConvert(value: HttpRequestAdapter): LocalDateTime? {
        return null
    }
    //    override fun canConvert(methodParameter: MethodParameter, value: HttpRequestAdapter): Boolean {
//        return methodParameter.param.type ==LocalDateTime::class.java
//    }
//
//    override fun typeConvert(value: String): LocalDateTime? {
//        return LocalDateTime.now()
//    }
}