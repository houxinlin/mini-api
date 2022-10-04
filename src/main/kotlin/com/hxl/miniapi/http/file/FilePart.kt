package com.hxl.miniapi.http.file

import sun.security.util.Length
import java.io.ByteArrayInputStream

class FilePart {
    lateinit var name: String
    lateinit var contentType: String
    lateinit var inputStream: ByteArrayInputStream
    var contentTypeLength: Long = -1
    override fun toString(): String {
        return "FilePart(name='$name', contentType='$contentType', contentTypeLength=$contentTypeLength)"
    }

}