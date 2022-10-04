package com.hxl.miniapi.core.convert

import com.hxl.miniapi.core.HttpParameterTypeConverter
import com.hxl.miniapi.core.MethodParameter
import com.hxl.miniapi.http.HttpRequestAdapter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HttpParameterLocalDateTypeConverter:HttpParameterTypeConverter<LocalDate> {
    private val regex="^[0-9]{4}-(((0[13578]|(10|12))-(0[1-9]|[1-2][0-9]|3[0-1]))|(02-(0[1-9]|[1-2][0-9]))|((0[469]|11)-(0[1-9]|[1-2][0-9]|30)))\$".toRegex()

    //    override fun canConvert(methodParameter: MethodParameter, value: HttpRequestAdapter): Boolean {
//        return methodParameter.param.type==LocalDate::class.java && regex.matches(value)
//    }
//
//    override fun typeConvert(value: String): LocalDate? {
//        return LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
//    }
    override fun canConvert(methodParameter: MethodParameter, request: HttpRequestAdapter): Boolean {
        return methodParameter.param.type==LocalDate::class.java
    }

    override fun typeConvert(value: HttpRequestAdapter): LocalDate? {
        return null
    }
}