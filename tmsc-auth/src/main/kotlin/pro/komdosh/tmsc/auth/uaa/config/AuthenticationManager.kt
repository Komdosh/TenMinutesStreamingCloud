package pro.komdosh.tmsc.auth.uaa.config

import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import pro.komdosh.tmsc.auth.uaa.config.token.TokenService
import pro.komdosh.tmsc.auth.uaa.user.UserRepository
import pro.komdosh.tmsc.auth.uaa.user.model.UserPrincipal
import pro.komdosh.tmsc.auth.uaa.user.model.UserRole
import reactor.core.publisher.Mono

@Component
class AuthenticationManager(
    private val tokenService: TokenService,
    private val userRepository: UserRepository
) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        return Mono.just(authentication.credentials.toString())
            .filterWhen(tokenService::validateAccessToken)
            .flatMap { accessToken ->
                tokenService.extractUserId(accessToken).flatMap { userId ->
                    tokenService.extractRoles(accessToken)
                        .map { SimpleGrantedAuthority(UserRole.valueOf(it).authority()) }
                        .collectList()
                        .flatMap { authorities ->
                            Mono.justOrEmpty(userRepository.findByEmail(userId)).map {
                                UsernamePasswordAuthenticationToken(
                                    UserPrincipal.create(it),
                                    null,
                                    authorities
                                )
                            }
                        }
                }
            }
    }

}
