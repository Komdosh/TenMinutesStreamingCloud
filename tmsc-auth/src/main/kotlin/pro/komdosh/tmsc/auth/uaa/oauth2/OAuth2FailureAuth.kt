package pro.komdosh.tmsc.auth.uaa.oauth2

import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.DefaultServerRedirectStrategy
import org.springframework.security.web.server.ServerRedirectStrategy
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import java.net.URI


@Component
class OAuth2FailureAuth : ServerAuthenticationFailureHandler {
    private val redirectStrategy: ServerRedirectStrategy = DefaultServerRedirectStrategy()

    override fun onAuthenticationFailure(
        webFilterExchange: WebFilterExchange,
        exception: AuthenticationException
    ): Mono<Void> {
        val exchange = webFilterExchange.exchange
        var targetUrl = exchange.request.queryParams.getFirst(REDIRECT_URI_PARAM)
            ?: ERROR_REDIRECT_URI
        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
            .queryParam("error", exception.localizedMessage)
            .build().toUriString()

        return redirectStrategy.sendRedirect(exchange, URI.create(targetUrl))

    }
}
