package com.hxl.miniapi.core.io

import java.net.JarURLConnection
import java.net.URL


/**
* @description: 扫描jar中符合条件的class
* @date: 2022/10/5 上午8:52
*/

class JarResourceLoader:ResourceLoader {
    override fun getResources(location: String): List<URL> {
        val jarUrl =URL("jar:$location")
        val result = mutableListOf<URL>()

        val keyword = jarUrl.path.substring(2 + jarUrl.path.indexOf("!/"))
        val connection = jarUrl.openConnection()
        if (connection is JarURLConnection){
            val entries = connection.jarFile.entries()
            val jarFile = connection.jarFile
            while (entries.hasMoreElements()){
                val name = entries.nextElement().name
                if (name.startsWith(keyword) && name.endsWith(".class")){
                    result.add(URL("jar:file:${jarFile.name}!/${name}"))
                }
            }
        }
        return result
    }
}