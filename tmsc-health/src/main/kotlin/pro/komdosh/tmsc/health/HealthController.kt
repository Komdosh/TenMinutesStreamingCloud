package pro.komdosh.tmsc.health

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@RestController
class HealthController {

    @Value("\${info.service}")
    lateinit var service: String

    @RequestMapping("/health")
    fun hello(): Mono<String> {
        return Mono.just("Here is [$service] service ")
    }
}
