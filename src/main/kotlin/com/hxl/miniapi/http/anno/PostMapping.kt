package com.hxl.miniapi.http.anno

@Retention()
@Target(AnnotationTarget.FUNCTION)
annotation class PostMapping(val value:String)
