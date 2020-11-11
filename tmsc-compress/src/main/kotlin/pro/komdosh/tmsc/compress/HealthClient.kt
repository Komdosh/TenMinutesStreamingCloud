package pro.komdosh.tmsc.compress

import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import reactivefeign.spring.config.ReactiveFeignClient
import reactor.core.publisher.Mono

@Component
@ReactiveFeignClient(name = "tmsc-health")
interface HealthClient {

    @GetMapping("/health")
    fun hello(): Mono<String>

}
