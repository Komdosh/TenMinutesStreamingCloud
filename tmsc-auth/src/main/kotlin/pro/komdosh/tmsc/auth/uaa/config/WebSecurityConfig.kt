package pro.komdosh.tmsc.auth.uaa.config

import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher
import org.springframework.web.server.ServerWebExchange
import pro.komdosh.tmsc.auth.uaa.oauth2.OAuth2FailureAuth
import pro.komdosh.tmsc.auth.uaa.oauth2.OAuth2SuccessfulAuth
import reactor.core.publisher.Mono


@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class WebSecurityConfig(
    private val authenticationManager: AuthenticationManager,
    private val securityContextRepository: SecurityContextRepository,
    private val customAuthorizationRequestResolver: CustomAuthorizationRequestResolver,
    private val oAuth2SuccessfulAuth: OAuth2SuccessfulAuth,
    private val oAuth2FailureAuth: OAuth2FailureAuth
) {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .exceptionHandling()
            .authenticationEntryPoint { swe: ServerWebExchange, e: AuthenticationException? ->
                Mono.fromRunnable {
                    swe.response.statusCode = HttpStatus.UNAUTHORIZED
                }
            }
            .accessDeniedHandler { swe: ServerWebExchange, e: AccessDeniedException? ->
                Mono.fromRunnable {
                    swe.response.statusCode = HttpStatus.FORBIDDEN
                }
            }
            .and()
            .csrf().disable()
            .formLogin().disable()
            .httpBasic().disable()
            .authenticationManager(authenticationManager)
            .securityContextRepository(securityContextRepository)
            .authorizeExchange()
            .pathMatchers(HttpMethod.OPTIONS).permitAll()
            .pathMatchers(
                "/",
                "/error",
                "/favicon.ico",
                "/**/*.png",
                "/**/*.gif",
                "/**/*.svg",
                "/**/*.jpg",
                "/**/*.html",
                "/**/*.css",
                "/**/*.js"
            ).permitAll()
            .pathMatchers("/oauth2/**").permitAll()
            .pathMatchers(HttpMethod.POST, "/api/v1/uaa", "/api/v1/uaa/login").permitAll()
            .anyExchange().authenticated()
            .and()
            .oauth2Login { oauth2 ->
                oauth2.authenticationSuccessHandler(oAuth2SuccessfulAuth)
                oauth2.authenticationFailureHandler(oAuth2FailureAuth)
                oauth2.authorizationRequestResolver(customAuthorizationRequestResolver)
                oauth2.authenticationMatcher(PathPatternParserServerWebExchangeMatcher("/oauth2/code/{registrationId}"))
            }
            .build()
    }

}
