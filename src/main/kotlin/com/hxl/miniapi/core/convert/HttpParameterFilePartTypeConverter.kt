package com.hxl.miniapi.core.convert

import com.hxl.miniapi.core.HttpParameterTypeConverter
import com.hxl.miniapi.core.MethodParameter
import com.hxl.miniapi.http.HttpRequestAdapter
import com.hxl.miniapi.http.file.FilePart

class HttpParameterFilePartTypeConverter: HttpParameterTypeConverter<FilePart> {
    override fun canConvert(methodParameter: MethodParameter, request: HttpRequestAdapter): Boolean {
        return methodParameter.param.type==FilePart::class.java
    }

    override fun typeConvert(value: HttpRequestAdapter): FilePart? {
        return null
    }
    //    override fun canConvert(methodParameter: MethodParameter, value: HttpRequestAdapter): Boolean {
//        return methodParameter.param.type==FilePart::class.java
//
//    }
//
//    override fun typeConvert(value: String): FilePart? {
//        return null
//    }
}