package pro.komdosh.tmsc.compress

import org.springframework.cloud.netflix.ribbon.RibbonClient
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping

@FeignClient(name = "tmsc-auth")
@RibbonClient(name = "tmsc-auth")
interface AuthClient {

    @GetMapping("/")
    fun hello(): String

}
