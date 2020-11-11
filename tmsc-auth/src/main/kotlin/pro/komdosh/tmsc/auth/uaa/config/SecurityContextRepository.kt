package pro.komdosh.tmsc.auth.uaa.config

import org.apache.commons.lang3.StringUtils
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

const val ACCESS_TOKEN_NAME = "token"
const val TOKEN_PREFIX = "Bearer "

@Component
class SecurityContextRepository(
    private val authenticationManager: AuthenticationManager
) : ServerSecurityContextRepository {

    override fun save(swe: ServerWebExchange, sc: SecurityContext): Mono<Void> {
        return Mono.empty()
    }

    override fun load(swe: ServerWebExchange): Mono<SecurityContext> {
        val jwt = getJwtFromRequest(swe.request)
        return if (StringUtils.isNotBlank(jwt)) {
            val auth = UsernamePasswordAuthenticationToken(null, jwt)
            authenticationManager.authenticate(auth)
                .map { authentication -> SecurityContextImpl(authentication) }
        } else {
            Mono.empty()
        }
    }

    private fun getJwtFromRequest(request: ServerHttpRequest): String {
        var bearerToken: String = request.headers.getFirst(HttpHeaders.AUTHORIZATION) ?: ""
        if (StringUtils.isEmpty(bearerToken)
            && request.queryParams[ACCESS_TOKEN_NAME]?.isNotEmpty() == true
            && StringUtils.isNotEmpty(request.queryParams[ACCESS_TOKEN_NAME]?.first())
        ) {
            bearerToken = request.queryParams[ACCESS_TOKEN_NAME]?.first() ?: ""
        }

        return if (StringUtils.isNotEmpty(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            bearerToken.substring(TOKEN_PREFIX.length)
        } else {
            bearerToken
        }
    }
}
