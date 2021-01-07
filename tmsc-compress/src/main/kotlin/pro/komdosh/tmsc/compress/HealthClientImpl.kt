package pro.komdosh.tmsc.compress

import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class HealthClientImpl : HealthClient {

    override fun hello(): Mono<String> {
        return WebClient
            .builder()
            .baseUrl("http://127.0.0.1:8002/health")
            .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .build().get().uri("/health").retrieve().bodyToMono(String::class.java)
    }

}
