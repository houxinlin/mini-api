package com.hxl.miniapi.core


interface  HttpParameterTypeConverter<T>:TypeConverter<String,T> {
     fun canConvert(methodParameter: MethodParameter,value: String):Boolean

}