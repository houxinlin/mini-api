package com.hxl.miniapi.test.controller

import org.apache.ibatis.annotations.Select

interface Test {
    @Select("select * from aunt_day")
    fun select():List<User>
}