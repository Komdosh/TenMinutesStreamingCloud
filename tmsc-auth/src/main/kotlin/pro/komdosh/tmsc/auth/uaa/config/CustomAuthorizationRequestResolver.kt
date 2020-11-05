package pro.komdosh.tmsc.auth.uaa.config

import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import pro.komdosh.tmsc.auth.uaa.oauth2.ISSUER_PARAM
import pro.komdosh.tmsc.auth.uaa.oauth2.TOKEN_PARAM
import pro.komdosh.tmsc.auth.uaa.user.AuthProvider
import pro.komdosh.tmsc.auth.uaa.user.UserPrincipal
import pro.komdosh.tmsc.auth.uaa.user.UserService
import reactor.core.publisher.Mono

@Component
class CustomAuthorizationRequestResolver(
    clientRegistrationRepository: ReactiveClientRegistrationRepository,
    private val appProperties: AppProperties,
    private val userService: UserService,
    private val jwtTokenService: JwtTokenService,
) : DefaultServerOAuth2AuthorizationRequestResolver(
    clientRegistrationRepository,
    PathPatternParserServerWebExchangeMatcher(
        appProperties.oauth2.baseUri
    )
), ServerOAuth2AuthorizationRequestResolver {

    private val noneAuthUri: String = "not available"

    override fun resolve(exchange: ServerWebExchange): Mono<OAuth2AuthorizationRequest> {
        val authorizationRequest = super.resolve(exchange)

        val issIsPresent = exchange.request.queryParams.getFirst(ISSUER_PARAM) != null
        return PathPatternParserServerWebExchangeMatcher(appProperties.oauth2.baseUri).matches(
            exchange
        ).flatMap { matchResult ->
            if (matchResult.isMatch && authorizationRequest == null && issIsPresent) {
                processNonOauth()
            } else authorizationRequest
        }
    }

    override fun resolve(
        exchange: ServerWebExchange,
        clientRegistrationId: String
    ): Mono<OAuth2AuthorizationRequest> {
        return super.resolve(exchange, clientRegistrationId)
    }

    private fun processNonOauth(): Mono<OAuth2AuthorizationRequest> {
        return userService
            .createUserWithoutUserInfoUri(
                OAuth2AccessToken(
                    OAuth2AccessToken.TokenType.BEARER,
                    "no-token",
                    null,
                    null
                )
            ).map {
                val oAuth2User = userService.processOAuth2User(AuthProvider.LOCAL.name, it)

                val user = (oAuth2User as UserPrincipal).user
                val attributes: MutableMap<String, Any> = HashMap()
                attributes[OAuth2ParameterNames.REGISTRATION_ID] = AuthProvider.LOCAL.name
                val builder: OAuth2AuthorizationRequest.Builder =
                    OAuth2AuthorizationRequest.authorizationCode()
                return@map builder
                    .clientId(AuthProvider.LOCAL.name)
                    .authorizationUri(noneAuthUri)
                    .redirectUri(AuthProvider.LOCAL.name)
                    .scopes(setOf(AuthProvider.LOCAL.name))
                    .state("")
                    .attributes(attributes)
                    .additionalParameters(
                        mapOf(
                            Pair(
                                TOKEN_PARAM,
                                jwtTokenService.generateToken(user)
                            )
                        )
                    )
                    .build()
            }
    }
}
