package pro.komdosh.tmsc.auth

import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
// This will allow us to reinitialize this controller to get any new config
// values when the /refresh endpoint is POSTed to.
@RefreshScope
class AppController {

    @Value("\${info.service}")
    lateinit var service: String

    @RequestMapping("/")
    fun hello(): String {
        return "Using [$service] from config server"
    }
}
