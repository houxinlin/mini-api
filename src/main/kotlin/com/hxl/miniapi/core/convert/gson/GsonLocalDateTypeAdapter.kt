package com.hxl.miniapi.core.convert.gson

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GsonLocalDateTypeAdapter:JsonSerializer<LocalDate> {
    override fun serialize(src: LocalDate?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src!!.format(DateTimeFormatter.ISO_LOCAL_DATE))
    }

}