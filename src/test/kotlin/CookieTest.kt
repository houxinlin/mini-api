import com.hxl.miniapi.api.CoolMini
import com.hxl.miniapi.core.MiniContext

class CookieTest {
}

fun main() {
    var miniContext = CoolMini(7070)
    miniContext.start(CookieTest::class.java)
}