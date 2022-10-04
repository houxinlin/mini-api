package com.hxl.miniapi.test.controller

import com.hxl.miniapi.http.anno.GetMapping
import com.hxl.miniapi.http.anno.PostMapping
import com.hxl.miniapi.http.anno.RestController
import com.hxl.miniapi.http.anno.param.PathVariable
import com.hxl.miniapi.http.anno.param.RequestParam
import com.hxl.miniapi.http.anno.param.RequestUri
import com.hxl.miniapi.http.file.FilePart
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.time.LocalDate

@RestController
class IndexController {
    @GetMapping("index")
    fun index():InputStream{
        return FileInputStream(File("/home/hxl/下载/okhttp-4.10.0.jar"))
    }
}