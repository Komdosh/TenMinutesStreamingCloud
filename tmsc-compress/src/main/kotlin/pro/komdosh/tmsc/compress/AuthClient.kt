package pro.komdosh.tmsc.compress

import org.springframework.cloud.netflix.ribbon.RibbonClient
import org.springframework.web.bind.annotation.GetMapping
import reactivefeign.spring.config.ReactiveFeignClient
import reactor.core.publisher.Mono

@ReactiveFeignClient(name = "tmsc-auth")
interface AuthClient {

    @GetMapping("/")
    fun hello(): Mono<String>

}
