package pro.komdosh.tmsc.proxy

import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.zuul.EnableZuulProxy

val log = KotlinLogging.logger {}

@EnableZuulProxy
@SpringBootApplication
class TmscProxyApplication

fun main(args: Array<String>) {
    runApplication<TmscProxyApplication>(*args)
}
