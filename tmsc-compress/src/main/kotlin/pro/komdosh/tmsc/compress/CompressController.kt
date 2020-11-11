package pro.komdosh.tmsc.compress

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@RestController
class CompressController(private val healthClient: HealthClient) {

    @Value("\${info.service}")
    lateinit var service: String

    @RequestMapping("/hello")
    fun hello(): Mono<String> {
        return healthClient.hello().map { s -> "$s and [${service}] service also works" }
    }
}
