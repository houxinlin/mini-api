import com.hxl.miniapi.api.CoolMini

class KotlinTest {
}

fun main() {
    val coolMini = CoolMini(4040)
    coolMini.whithKotlin {
        configDatabase {
            userName ="root"
            password="hxl495594.."
            url="jdbc:mysql://localhost:3306/db_inner"
        }
        get("get") {
        }
        delete("delete"){
        }
        put("put"){
        }
    }
    coolMini.start(KotlinTest::class.java)
}