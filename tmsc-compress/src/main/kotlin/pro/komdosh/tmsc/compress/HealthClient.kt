package pro.komdosh.tmsc.compress

import org.springframework.web.bind.annotation.GetMapping
import reactivefeign.spring.config.ReactiveFeignClient
import reactor.core.publisher.Mono

@ReactiveFeignClient(name = "tmsc-health", url = "http://127.0.0.1:8002/health")
interface HealthClient {

    @GetMapping("/health")
    fun hello(): Mono<String>

}
