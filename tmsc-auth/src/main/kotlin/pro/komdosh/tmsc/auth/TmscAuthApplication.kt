package pro.komdosh.tmsc.auth

import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

val log = KotlinLogging.logger {}

@SpringBootApplication
class TmscAuthApplication

fun main(args: Array<String>) {
    runApplication<TmscAuthApplication>(*args)
}
