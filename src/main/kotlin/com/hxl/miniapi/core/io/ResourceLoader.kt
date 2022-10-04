package com.hxl.miniapi.core.io

import java.net.URL

interface ResourceLoader {
    fun getResources(locationPattern: String): List<URL>
}