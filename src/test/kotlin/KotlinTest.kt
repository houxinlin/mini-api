import com.hxl.miniapi.api.CoolMini

class KotlinTest {
}

fun main() {
    val coolMini = CoolMini(4040)

    coolMini.withKotlin {
        configDatabase {
            userName ="root"
            password="xx"
            url="jdbc:mysql://localhost:3306/db_inner"
        }
        interceptor{
            includePatterns= arrayListOf("*")
            excludePatterns= arrayListOf("put")
            run{
                println(httpRequest.getRequestPath())
                httpResponse.getOutputStream().write("拦截".toByteArray())
                false
            }
        }
        get("get") {
            println(runSqlListMap("select * from tb_user"))
        }
        delete("delete"){
        }
        get("put"){
            println("test")
        }
    }
    coolMini.start(KotlinTest::class.java)
}