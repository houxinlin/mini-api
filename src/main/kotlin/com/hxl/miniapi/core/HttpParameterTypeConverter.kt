package com.hxl.miniapi.core

import com.hxl.miniapi.http.HttpRequestAdapter

interface  HttpParameterTypeConverter<T>:TypeConverter<HttpRequestAdapter,T> {

     fun canConvert(methodParameter: MethodParameter,request: HttpRequestAdapter):Boolean

}