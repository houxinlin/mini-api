import com.hxl.miniapi.api.CoolMini
import com.hxl.miniapi.http.anno.GetMapping
import com.hxl.miniapi.http.anno.RestController
import com.hxl.miniapi.http.anno.param.RequestUri
import com.hxl.miniapi.orm.AutowriteCrud
import com.hxl.miniapi.orm.mybatis.IMybatisCrudRepository
import com.hxl.miniapi.orm.mybatis.MysqlDataSource
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select

class DatabaseTest {
}

@RestController
class DatabaseTestController {
    interface Mapper {
        @Select("select * from tb_user where user_name =#{name}")
        fun list(@Param("name") name: String): List<Map<String, String>>
    }

    @AutowriteCrud
    lateinit var mybatisCrudRepository: IMybatisCrudRepository

    @GetMapping("test")
    fun test(@RequestUri string: String): String {
        val a = mybatisCrudRepository.listMap("select * from tb_user ")
        return string
    }
}

fun main() {
    val miniContext = CoolMini(7070)
    miniContext.registerController(DatabaseTestController::class.java)
    miniContext.setDataSource(MysqlDataSource("root", "xx", "jdbc:mysql://localhost:3306/db_inner"))

    miniContext.start(DatabaseTest::class.java)
}