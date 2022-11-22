import com.hxl.miniapi.api.CoolMini

class KotlinTest {
}

fun main() {
    val coolMini = CoolMini(4040)
    coolMini.withKotlin {
        configDatabase {
            userName ="root"
            password=""
            url="jdbc:mysql://localhost:3306/db_inner"
        }
        get("get") {
            runSqlListMap("select * from tb_user")
        }
        delete("delete"){
        }
        put("put"){
        }
    }
    coolMini.start(KotlinTest::class.java)
}