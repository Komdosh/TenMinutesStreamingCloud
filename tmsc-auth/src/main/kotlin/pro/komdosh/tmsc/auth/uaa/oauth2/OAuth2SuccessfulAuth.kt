package pro.komdosh.tmsc.auth.uaa.oauth2

import org.springframework.security.core.Authentication
import org.springframework.security.web.server.DefaultServerRedirectStrategy
import org.springframework.security.web.server.ServerRedirectStrategy
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.util.UriComponentsBuilder
import pro.komdosh.tmsc.auth.log
import pro.komdosh.tmsc.auth.uaa.config.AppProperties
import pro.komdosh.tmsc.auth.uaa.config.token.TokenService
import pro.komdosh.tmsc.auth.uaa.user.model.UserPrincipal
import reactor.core.publisher.Mono
import java.net.URI

@Component
class OAuth2SuccessfulAuth(
    private val tokenService: TokenService,
    private val appProperties: AppProperties
) : RedirectServerAuthenticationSuccessHandler() {
    private val redirectStrategy: ServerRedirectStrategy = DefaultServerRedirectStrategy()

    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange,
        authentication: Authentication
    ): Mono<Void> {
        val targetUrl = determineTargetUrl(webFilterExchange.exchange, authentication)
        if (webFilterExchange.exchange.response.isCommitted) {
            log.debug("Response has already been committed. Unable to redirect to $targetUrl")
            return Mono.empty()
        }



        return redirectStrategy.sendRedirect(webFilterExchange.exchange, targetUrl)
    }

    private fun isAuthorizedRedirectUri(uri: String): Boolean {
        val clientRedirectUri = URI.create(uri)
        return appProperties.oauth2.authorizedRedirectUris
            .stream()
            .anyMatch { authorizedRedirectUri ->
                // Only validate host and port. Let the clients use different paths if they want to
                val authorizedURI = URI.create(authorizedRedirectUri)
                (authorizedURI.host.equals(clientRedirectUri.host, ignoreCase = true)
                        && authorizedURI.port == clientRedirectUri.port)
            }
    }

    fun determineTargetUrl(
        swe: ServerWebExchange,
        authentication: Authentication
    ): URI {
        val redirectUri = swe.request.queryParams.getFirst(REDIRECT_URI_PARAM)

        if (redirectUri != null && !isAuthorizedRedirectUri(redirectUri)) {
            throw IllegalStateException("Server got an Unauthorized Redirect URI and can't be proceed with the authentication");
        }

        val userPrincipal = authentication.principal as UserPrincipal

        val targetUrl: String = redirectUri ?: DEFAULT_REDIRECT_URI

        val token: String = tokenService.generateAccessToken(userPrincipal.user)
        val uriComponentsBuilder = UriComponentsBuilder.fromUriString(targetUrl)
            .queryParam(TOKEN_PARAM, token)

        return uriComponentsBuilder.build().toUri()
    }

}
