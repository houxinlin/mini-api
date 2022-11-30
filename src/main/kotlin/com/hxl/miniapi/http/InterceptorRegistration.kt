package com.hxl.miniapi.http

import com.hxl.miniapi.utils.addPrefixIfMiss

class InterceptorRegistration(val intercept: HttpIntercept) {
    private val includePatterns: MutableList<String> = mutableListOf()
    private val excludePatterns: MutableList<String> = mutableListOf()
    fun includePathPatterns(patterns: List<String>): InterceptorRegistration {
        patterns.forEach { this.includePatterns.add(it.addPrefixIfMiss("/")) }
        return this
    }

    fun excludePathPatterns(patterns: List<String>): InterceptorRegistration {
        patterns.forEach { this.excludePatterns.add(it.addPrefixIfMiss("/")) }
        return this
    }

    fun getIncludePatterns(): List<String> = this.includePatterns
    fun getExcludePatterns(): List<String> = this.excludePatterns
}