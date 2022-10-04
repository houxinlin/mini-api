package com.hxl.miniapi.core.convert

import com.hxl.miniapi.utils.isString

class SimpleTypeConverter {
    fun typeConvert(requireClass: Class<*>, value: String): Any {
        if (requireClass.isString()) return value
        return typeConversters[requireClass]!!.invoke(value)
    }

    private val typeConversters: MutableMap<Class<*>, (String) -> Any> = mutableMapOf(
        Int::class.java to { it.toInt() },
        Short::class.java to { it.toShort() },
        Float::class.java to { it.toFloat() },
        Double::class.java to { it.toDouble() },
        Boolean::class.java to {
            if (it.lowercase() == "true" || it.lowercase() == "false") {
                it.toBoolean()

            } else {
                if (it != "0" && it != "1") {
                    false
                } else {
                    it != "0"
                }
            }

        },
        Byte::class.java to { it.toByte() },
        Long::class.java to { it.toLong() },
        Char::class.java to {
            if (it.isNotEmpty()) {
                it.toCharArray()[0]
            } else {
                0x0.toChar()
            }
        },
    )
}