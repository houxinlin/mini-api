import com.hxl.miniapi.api.CoolMini
import com.hxl.miniapi.http.anno.PostMapping
import com.hxl.miniapi.http.anno.RestController
import com.hxl.miniapi.http.anno.param.RequestBody
import com.hxl.miniapi.http.anno.param.RequestParam
import com.hxl.miniapi.http.file.FilePart
import com.hxl.miniapi.http.response.HttpResponse

class FileTest {
}
@RestController
class FileTestController{
    @PostMapping("test")
    fun test(@RequestParam("name")name:String, @RequestParam("file")file:FilePart): String {
        println(file.contentType)
        println(name)
        println(file.inputStream.readBytes().decodeToString())
        return "OK"
    }
}
fun main() {
    val miniContext = CoolMini(7070)
    miniContext.registerController(FileTestController::class.java)
    miniContext.start(CookieTest::class.java)
}