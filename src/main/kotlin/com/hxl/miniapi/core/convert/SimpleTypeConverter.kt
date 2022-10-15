package com.hxl.miniapi.core.convert

import com.hxl.miniapi.utils.isString
import kotlin.reflect.KClass

class SimpleTypeConverter {
    fun typeConvert(requireClass: Class<*>, value: String): Any {
        if (requireClass.isString()) return value
        return typeConversters[requireClass.kotlin]!!.invoke(value)
    }

    fun canConvert(requireClass: Class<*>, value: String): Boolean {
        return typeConversters.containsKey(requireClass.kotlin)
    }

    private val typeConversters: MutableMap<KClass<*>, (String) -> Any> = mutableMapOf(
        Int::class to { it.toInt() },
        Short::class to { it.toShort() },
        Float::class to { it.toFloat() },
        Double::class to { it.toDouble() },
        Boolean::class to {
            if (it.lowercase() == "true" || it.lowercase() == "false") {
                it.toBoolean()
            } else {
                if (it != "0" && it != "1") false else it != "0"
            }

        },
        Byte::class to { it.toByte() },
        Long::class to { it.toLong() },
        Char::class to {
            if (it.isNotEmpty()) it.toCharArray()[0] else 0x0.toChar()
        },
    )
}