package pro.komdosh.tmsc.auth.uaa.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
@ConfigurationProperties(prefix = "app")
class AppProperties {
    val auth = Auth()
    val oauth2 = OAuth2()

    class Auth {
        var tokenSecret: String? = null
        var tokenExpirationMsec: Long = 0
        var refreshTokenExpirationMsec: Long = 0
    }

    class OAuth2 {
        val authorizedRedirectUris: List<String> = ArrayList()

        var baseUri: String = ""
    }

}
