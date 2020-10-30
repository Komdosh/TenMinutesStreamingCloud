package pro.komdosh.tmsc.discovery

import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

val log = KotlinLogging.logger {}

@EnableEurekaServer
@SpringBootApplication
class TmscDiscoveryApplication

fun main(args: Array<String>) {
    runApplication<TmscDiscoveryApplication>(*args)
}
