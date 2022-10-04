package com.hxl.miniapi.core.convert

import com.hxl.miniapi.core.HttpParameterTypeConverter
import com.hxl.miniapi.core.MethodParameter
import java.text.DateFormat
import java.util.Date

class HttpParameterDateTypeConverter: HttpParameterTypeConverter<Date> {

    override fun canConvert(methodParameter: MethodParameter, value: String): Boolean {
        return  methodParameter.param.type==Date::class.java
    }

    override fun typeConvert(value: String): Date? {
        return DateFormat.getDateTimeInstance().parse(value)
    }
}