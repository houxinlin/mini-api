package controller

import com.hxl.miniapi.http.anno.GetMapping
import com.hxl.miniapi.http.anno.RestController
import com.hxl.miniapi.http.anno.param.RequestParam
import com.hxl.miniapi.http.cookie.Cookie
import com.hxl.miniapi.http.request.HttpRequest
import com.hxl.miniapi.http.response.HttpResponse
import com.hxl.miniapi.http.session.Session

@RestController
class Controller {

    @GetMapping("set")
    fun set(@RequestParam("file") filePart: String,
            httpRequest: HttpRequest,
            httpResponse: HttpResponse,
            session: Session,
            @RequestParam("page") page: Int):String{
        session.setAttribute("User","b")
        session.setTnvalidTime(5*1000)
        return "ok"
    }
    @GetMapping("get")
    fun get(@RequestParam("file") filePart: String,
            httpRequest: HttpRequest,
            httpResponse: HttpResponse,
            session: Session,
            @RequestParam("page") page: Int):String{
        println(session.getAttibute("a","s"))

        return "ok"
    }
}