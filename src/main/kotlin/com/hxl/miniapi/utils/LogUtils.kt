package com.hxl.miniapi.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class LogUtils {
    companion object{
    }
}
var logCache = mutableMapOf<String,Logger>()
fun String.logInfo(){
    val stackTrace = Thread.currentThread().stackTrace
    val className =if (stackTrace.size<2) stackTrace[0].className else stackTrace[2].className
    logCache.getOrPut(className) { LoggerFactory.getLogger(className) }.info(this)
}
fun String.errorInfo(){
    val stackTrace = Thread.currentThread().stackTrace
    val className =if (stackTrace.size<2) stackTrace[0].className else stackTrace[2].className
    logCache.getOrPut(className) { LoggerFactory.getLogger(className) }.error(this)
}