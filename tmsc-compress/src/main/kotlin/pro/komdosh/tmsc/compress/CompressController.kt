package pro.komdosh.tmsc.compress

import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class CompressController(private val authClient: AuthClient) {

    @Value("\${info.service}")
    lateinit var service: String

    @RequestMapping("/")
    fun hello(): Mono<String> {
        return authClient.hello().map { s-> "$s and [${service}] for that" }
    }
}
