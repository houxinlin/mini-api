package com.hxl.miniapi.core

import java.lang.reflect.Method
import java.lang.reflect.Parameter

class MethodParameter {

    /**
    * @description: 方法
    * @date: 2022/10/2 下午5:16
    */

    lateinit var method:Method


    /**
    * @description: 参数上的方法
    * @date: 2022/10/2 下午5:17
    */

    var parameterAnnotations = mutableListOf<Annotation>()


    /**
    * @description: 参数名字
    * @date: 2022/10/2 下午5:17
    */

    lateinit var parameterName :String

    /**
     * 参数
     */
    lateinit var param:Parameter


    fun hasAnnotation(annotation: Class<out Annotation>):Boolean{
        for (parameterAnnotation in parameterAnnotations) {
            if (parameterAnnotation.annotationClass.java == annotation) {
                return true
            }
        }
        return false
    }
    fun <A> getAnnotation(annotationType: Class<A>?): A? {
        for (parameterAnnotation in parameterAnnotations) {
            if (parameterAnnotation.annotationClass.java == annotationType) {
                return parameterAnnotation as (A)
            }
        }
        return null
    }
}