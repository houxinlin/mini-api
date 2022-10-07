package com.hxl.miniapi.core.io

import java.net.URL

interface ResourceLoader {
    fun getResources(location: String): List<URL>
}