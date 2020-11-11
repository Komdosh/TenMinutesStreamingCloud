package pro.komdosh.tmsc.health

import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients
import reactivefeign.spring.config.EnableReactiveFeignClients

val log = KotlinLogging.logger {}

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@EnableReactiveFeignClients
class TmscHealthApplication {

}

fun main(args: Array<String>) {
    runApplication<TmscHealthApplication>(*args)
}
