package com.hxl.miniapi.core.convert

import com.hxl.miniapi.core.HttpParameterTypeConverter
import com.hxl.miniapi.core.MethodParameter
import com.hxl.miniapi.http.HttpRequestAdapter
import java.text.SimpleDateFormat
import java.util.Date

class HttpParameterDateTypeConverter: HttpParameterTypeConverter<Date> {
    override fun canConvert(methodParameter: MethodParameter, request: HttpRequestAdapter): Boolean {
        return  methodParameter.param.type==Date::class.java
    }

    override fun typeConvert(value: HttpRequestAdapter): Date? {
        return null
    }

    //    override fun canConvert(methodParameter: MethodParameter, value: HttpRequestAdapter): Boolean {
//        return  methodParameter.param.type==Date::class.java
//    }
//
//    override fun typeConvert(value: String): Date? {
//        val pattern = "yyyy-MM-dd HH-mm-ss";
//        return SimpleDateFormat(pattern).parse(pattern)
//    }
}