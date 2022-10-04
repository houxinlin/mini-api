package com.hxl.miniapi.core.io

import com.hxl.miniapi.utils.FileUtils
import java.net.URL

class FileResourceLoader :ResourceLoader {
    override fun getResources(locationPattern: String): List<URL> {
        return FileUtils.listFile(locationPattern)
            .filter { it.name.endsWith(".class") }
            .map { it.toURI().toURL() }
            .toCollection(mutableListOf())
    }
}