package com.hxl.miniapi.http

class InterceptorRegistration(val intercept: HttpIntercept) {
    val includePatterns: MutableList<String> = mutableListOf()
    val excludePatterns: MutableList<String> = mutableListOf()
    fun addPathPatterns(patterns: List<String>): InterceptorRegistration {
        this.includePatterns.addAll(patterns)
        return this
    }

    fun excludePathPatterns(patterns: List<String>): InterceptorRegistration {
        this.excludePatterns.addAll(patterns)
        return this
    }
}