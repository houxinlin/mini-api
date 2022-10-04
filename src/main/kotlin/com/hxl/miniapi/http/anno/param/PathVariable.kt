package com.hxl.miniapi.http.anno.param

@Retention()
@Target( AnnotationTarget.VALUE_PARAMETER)
annotation class PathVariable(val value:String)
