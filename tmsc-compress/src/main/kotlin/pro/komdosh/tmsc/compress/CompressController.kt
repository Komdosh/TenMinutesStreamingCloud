package pro.komdosh.tmsc.compress

import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CompressController(private val authClient: AuthClient) {

    @Value("\${info.service}")
    lateinit var service: String

    @RequestMapping("/")
    fun hello(): String {
        return "${authClient.hello()} and [${service}] for that"
    }
}
