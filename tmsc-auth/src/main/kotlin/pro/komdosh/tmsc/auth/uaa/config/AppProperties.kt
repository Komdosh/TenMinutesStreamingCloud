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
        val tokenSecret: String? = null
        val tokenExpirationMsec: Long = 0
    }

    class OAuth2 {
        val authorizedRedirectUris: List<String> = ArrayList()

        val baseUri: String = ""
    }

}
