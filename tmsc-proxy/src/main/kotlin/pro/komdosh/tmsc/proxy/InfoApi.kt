package pro.komdosh.tmsc.proxy

import org.springframework.cloud.netflix.zuul.filters.ZuulProperties
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AppController(
    private val properties: ZuulProperties
) {

    @RequestMapping("/")
    fun hello(): String {
        log.info(properties.routes.values.joinToString(",") { s -> s.serviceId })
        return properties.routes.values.joinToString(",") { s -> s.serviceId }
    }
}
