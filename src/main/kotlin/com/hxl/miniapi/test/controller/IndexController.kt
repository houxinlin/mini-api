package com.hxl.miniapi.test.controller

import com.hxl.miniapi.http.anno.GetMapping
import com.hxl.miniapi.http.anno.PostMapping
import com.hxl.miniapi.http.anno.RestController
import com.hxl.miniapi.http.anno.param.RequestParam
import com.hxl.miniapi.http.session.Session
import com.hxl.miniapi.orm.AutowriteCrud
import com.hxl.miniapi.orm.BaseCrudRepository
import com.hxl.miniapi.orm.MybatisCrudRepository
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.time.LocalDateTime
import java.util.Date

@RestController
class IndexController {

    @AutowriteCrud
    lateinit var baseCrudRepository: MybatisCrudRepository
    @PostMapping("index")
    fun index(@RequestParam("timer") session: Date){
        var test = baseCrudRepository.getMapper(Test::class.java)

        println(test.select())
    }

}