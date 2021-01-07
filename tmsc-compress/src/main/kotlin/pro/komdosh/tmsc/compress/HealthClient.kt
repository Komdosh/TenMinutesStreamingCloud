package pro.komdosh.tmsc.compress

import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
interface HealthClient {

    fun hello(): Mono<String>

}
