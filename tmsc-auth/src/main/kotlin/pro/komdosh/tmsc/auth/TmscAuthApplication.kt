package pro.komdosh.tmsc.auth

import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

val log = KotlinLogging.logger {}

@EnableDiscoveryClient
@SpringBootApplication
class TmscAuthApplication

fun main(args: Array<String>) {
    runApplication<TmscAuthApplication>(*args)
}
