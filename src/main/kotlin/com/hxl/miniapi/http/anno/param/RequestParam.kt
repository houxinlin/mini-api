package com.hxl.miniapi.http.anno.param
@Retention()
@Target( AnnotationTarget.VALUE_PARAMETER)
annotation class RequestParam( val value:String) {
}