package com.hxl.miniapi.test

import com.hxl.miniapi.api.CoolMini
import com.hxl.miniapi.orm.Mybatis
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.datasource.pooled.PooledDataSource
import org.apache.ibatis.executor.result.DefaultResultHandler
import org.apache.ibatis.mapping.Environment
import org.apache.ibatis.mapping.MappedStatement
import org.apache.ibatis.mapping.ResultMap
import org.apache.ibatis.mapping.SqlCommandType
import org.apache.ibatis.scripting.defaults.RawSqlSource
import org.apache.ibatis.session.Configuration
import org.apache.ibatis.session.RowBounds
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.apache.ibatis.transaction.TransactionFactory
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory
import javax.sql.DataSource


class Main {
}

fun main() {
    val coolMini = CoolMini(7070)
    coolMini.start(Main::class.java)

}