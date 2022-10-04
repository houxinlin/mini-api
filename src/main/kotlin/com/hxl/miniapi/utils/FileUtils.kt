package com.hxl.miniapi.utils

import java.io.File

object FileUtils {
    fun listFile(root: String): List<File> {
        return File(root).walk().toCollection(mutableListOf())
    }
}