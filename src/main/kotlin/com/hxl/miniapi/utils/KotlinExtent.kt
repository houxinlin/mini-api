package com.hxl.miniapi.utils

import com.hxl.miniapi.core.MappingInfo
import com.hxl.miniapi.http.HttpMethod
import com.hxl.miniapi.http.anno.DeleteMapping
import com.hxl.miniapi.http.anno.GetMapping
import com.hxl.miniapi.http.anno.PostMapping
import com.hxl.miniapi.http.anno.PutMapping
import java.lang.reflect.Method
import java.net.URLDecoder

class KotlinExtent {
}
// String-------------------------------
fun String.addPrefixIfMiss(prefix: String): String {
    return if (this.startsWith(prefix)) this else "$prefix$this"
}
fun String.urlArgumentToMap():Map<String,String>{
    val map = mutableMapOf<String,String>()
    this.split("&").map { it.split("=") }.forEach { if (it.size==2)  map[it[0]]=URLDecoder.decode(it[1],"utf-8") }
    return map
}
fun String.startWhithPlus(prefix:String):Boolean{
    if(this.startsWith(prefix)) return true
    if (this.startsWith(prefix.lowercase())) return true
    return false
}
//Class-------------------------------
fun Class<*>.getDefaultValue():Any?{
    if (this.kotlin==Int::class) return  0
    if (this.kotlin==Float::class) return  0.toFloat()
    if (this.kotlin==Double::class) return  0.toDouble()
    if (this.kotlin==Long::class) return  0.toLong()
    if (this.kotlin==Short::class) return 0.toShort()
    if (this.kotlin==Byte::class) return  0.toByte()
    if(this.kotlin ==Char::class) return 0x0.toChar()
    if (this.kotlin==Boolean::class) return  false
    return  null

}
fun Class<*>.isBaseType():Boolean{
    return this.kotlin==Int::class ||
            this.kotlin ==Float::class||
            this.kotlin ==Double::class||
            this.kotlin==Long::class||
            this.kotlin==Boolean::class||
            this.kotlin==Short::class||
            this.kotlin==Byte::class||
            this.kotlin==Char::class
}

fun Class<*>.isString():Boolean{
    return this.kotlin==String::class
}

//annotation-----------------------------

fun Annotation.getDefaultValue(): String {
    val valueMethod = this::class.java.getDeclaredMethod("value")
    return valueMethod.invoke(this) as String
}

//method--------------------------------
fun Method.getRequestMappingAnnotation(): Annotation {
    if (this.getDeclaredAnnotation(GetMapping::class.java) != null) return this.getDeclaredAnnotation(GetMapping::class.java)
    if (this.getDeclaredAnnotation(PostMapping::class.java) != null) return this.getDeclaredAnnotation(PostMapping::class.java)
    if (this.getDeclaredAnnotation(DeleteMapping::class.java) != null) return this.getDeclaredAnnotation(DeleteMapping::class.java)
    if (this.getDeclaredAnnotation(PutMapping::class.java) != null) return this.getDeclaredAnnotation(PutMapping::class.java)
    return null!!
}

fun Method.getRequestMappingInfo(): MappingInfo? {
    if (this.getDeclaredAnnotation(GetMapping::class.java) != null) {
        return MappingInfo().apply {
            this.httpMethod = HttpMethod.GET
        }
    } else if (this.getDeclaredAnnotation(PostMapping::class.java) != null) {
        return MappingInfo().apply {
            this.httpMethod = HttpMethod.POST
        }
    } else if (this.getDeclaredAnnotation(DeleteMapping::class.java) != null) {
        return MappingInfo().apply {
            this.httpMethod = HttpMethod.DELETE
        }
    } else if (this.getDeclaredAnnotation(PutMapping::class.java) != null) {
        return MappingInfo().apply {
            this.httpMethod = HttpMethod.PUT
        }
    }
    return null
}