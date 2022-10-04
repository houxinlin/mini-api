package com.hxl.miniapi.core.io

import java.net.URL

class JarResourceLoader:ResourceLoader {
    override fun getResources(locationPattern: String): List<URL> {
        return mutableListOf()
    }
}