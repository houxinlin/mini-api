package controller

import com.hxl.miniapi.http.anno.GetMapping
import com.hxl.miniapi.http.anno.RestController
import com.hxl.miniapi.http.anno.param.RequestParam
import com.hxl.miniapi.http.session.Session

@RestController
class Controller {
    @GetMapping("login")
    fun login(session: Session):String{
        var attributeKeys = session.getAttributeKeys()
        return "name"
    }
    @GetMapping("set")
    fun set(session: Session,@RequestParam("name")name:String):String{
        session.setAttribute("name","name")
        return "ok"
    }
}