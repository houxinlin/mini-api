package com.hxl.miniapi.core

interface TypeConverter<F,T>{
    fun  typeConvert(value: F): T?
}