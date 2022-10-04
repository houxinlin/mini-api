package com.hxl.miniapi.utils

object BeanUtils {
}
fun String.toClass():Class<*>{
    return Class.forName(this.replace("/","."))
}
fun Class<*>.instance():Any{
    return this.getConstructor().newInstance()
}