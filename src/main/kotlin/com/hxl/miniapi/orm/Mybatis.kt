package com.hxl.miniapi.orm

import org.apache.ibatis.executor.result.DefaultResultHandler
import org.apache.ibatis.executor.statement.StatementHandler
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
import java.sql.PreparedStatement
import javax.sql.DataSource

class Mybatis(private val dataSource: DataSource) {

    private val transactionFactory: TransactionFactory = JdbcTransactionFactory()
    private val environment = Environment("development", transactionFactory, dataSource)
    private val configuration = Configuration(environment)
    private val sqlSessionFactory = SqlSessionFactoryBuilder().build(configuration)
    private val session = sqlSessionFactory.openSession(true) //自动提交
    private val connection = session.connection
    private val executor = configuration.newExecutor(transactionFactory.newTransaction(connection))

    init {
        configuration.isMapUnderscoreToCamelCase=true
    }
    fun <T> getMapper(type: Class<T>): T {
        if (!configuration.hasMapper(type)) configuration.addMapper(type)
        return configuration.getMapper(type, session)
    }

    private fun applyParam(preparedStatement: PreparedStatement,vararg  arg:Any){
        for (index in arg.indices) {
            preparedStatement.setObject(index+1,arg[index])
        }
    }
    fun <K, V> queryMap(querySql: String,vararg  arg: Any): List<Map<K, V>>? {
        val statementHandler = createStatementHandler(querySql, Map::class.java, SqlCommandType.SELECT)
        val defaultResultHandler = DefaultResultHandler(configuration.objectFactory)
        val stmt = statementHandler.prepare(connection, 2000)
        applyParam(stmt as PreparedStatement,*arg)
        return statementHandler.query(stmt, defaultResultHandler)
    }


    fun <T> queryFormList(querySql: String, ofClass: Class<T>,vararg  arg: Any): List<T>? {
        val statementHandler = createStatementHandler(querySql, ofClass, SqlCommandType.SELECT)
        val defaultResultHandler = DefaultResultHandler(configuration.objectFactory)
        val stmt = statementHandler.prepare(connection, 2000)
        applyParam(stmt as PreparedStatement,*arg)
        return statementHandler.query<T>(stmt, defaultResultHandler)
    }

    fun update(querySql: String,vararg  arg: Any): Int {
        val statementHandler = createStatementHandler(querySql, Int::class.java, SqlCommandType.UPDATE)
        val stmt = statementHandler.prepare(connection, 2000)
        applyParam(stmt as PreparedStatement,*arg)
        return statementHandler.update(stmt)
    }
    private fun createResultMap(querySql: String, ofClass: Class<*>): ResultMap {
        return ResultMap.Builder(configuration, querySql, ofClass, mutableListOf())
            .build()
    }

    private fun createStatementHandler(querySql: String, ofClass: Class<*>, sqlCommandType: SqlCommandType): StatementHandler {
        val rawSqlSource = RawSqlSource(configuration, querySql, null)
        val statement = MappedStatement.Builder(configuration, querySql, rawSqlSource, sqlCommandType)
            .resultMaps(mutableListOf(createResultMap(querySql, ofClass)))
            .build()
        val boundSql = statement.getBoundSql(null)
        return configuration.newStatementHandler(executor, statement, null, RowBounds(), null, boundSql)
    }
}