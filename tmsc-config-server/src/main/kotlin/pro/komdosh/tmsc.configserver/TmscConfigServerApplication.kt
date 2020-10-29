package pro.komdosh.tmsc.configserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.config.server.EnableConfigServer

@EnableConfigServer
@SpringBootApplication
class TmscConfigServerApplication

fun main(args: Array<String>) {
    runApplication<TmscConfigServerApplication>(*args)
}
