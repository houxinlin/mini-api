package com.hxl.miniapi.core.convert

import com.hxl.miniapi.core.HttpParameterTypeConverter
import com.hxl.miniapi.core.MethodParameter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HttpParameteLocalDateTimeTypeConverter: HttpParameterTypeConverter<LocalDateTime> {
    override fun canConvert(methodParameter: MethodParameter, value: String): Boolean {
        return methodParameter.param.type ==LocalDateTime::class.java
    }

    override fun typeConvert(value: String): LocalDateTime? {
        return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }
}