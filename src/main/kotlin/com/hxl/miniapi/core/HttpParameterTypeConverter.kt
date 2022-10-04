package com.hxl.miniapi.core

import com.hxl.miniapi.http.HttpRequestAdapter

interface  HttpParameterTypeConverter<T>:TypeConverter<String,T> {

     fun canConvert(methodParameter: MethodParameter,value: String):Boolean

}