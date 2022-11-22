package mybatis

import com.hxl.miniapi.orm.mybatis.MysqlDataSource
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.mapping.Environment
import org.apache.ibatis.session.Configuration
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.apache.ibatis.transaction.TransactionFactory
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory

class MybatisTest {
}
interface  Mapper {
    @Select("select * from tb_user where user_name =#{name}")
    fun list(@Param("name")name:String):List<Map<String,String>>
}
fun main() {
     val transactionFactory: TransactionFactory = JdbcTransactionFactory()
     val environment = Environment("development", transactionFactory, MysqlDataSource("root", "hxl495594..", "jdbc:mysql://localhost:3306/db_inner"))
     val configuration = Configuration(environment)

    configuration.addMapper(Mapper::class.java)
    var build = SqlSessionFactoryBuilder().build(configuration).openSession().getMapper(Mapper::class.java)

}