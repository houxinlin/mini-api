package com.hxl.miniapi.core.convert.gson

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GsonLocalDateTimeTypeAdapter: JsonSerializer<LocalDateTime> {
    override fun serialize(src: LocalDateTime?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src!!.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
    }
}
