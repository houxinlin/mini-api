package com.hxl.miniapi.core.io

import com.hxl.miniapi.utils.FileUtils
import java.net.URL

/**
* @description: 从文件中扫描符合条件的class
* @date: 2022/10/5 上午8:53
*/

class FileResourceLoader :ResourceLoader {
    override fun getResources(location: String): List<URL> {
        return FileUtils.listFile(location)
            .filter { it.name.endsWith(".class",ignoreCase = true) }
            .map { it.toURI().toURL() }
            .toCollection(mutableListOf())
    }
}