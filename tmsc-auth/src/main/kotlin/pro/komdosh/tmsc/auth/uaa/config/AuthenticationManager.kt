package pro.komdosh.tmsc.auth.uaa.config

import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import pro.komdosh.tmsc.auth.uaa.user.UserPrincipal
import pro.komdosh.tmsc.auth.uaa.user.UserRepository
import reactor.core.publisher.Mono
import java.util.*

@Component
class AuthenticationManager(
    private val jwtTokenService: JwtTokenService,
    private val userRepository: UserRepository
) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        val authToken = authentication.credentials.toString()

        return try {
            if (!jwtTokenService.validateToken(authToken)) {
                return Mono.empty()
            }
            val claims = jwtTokenService.getAllClaimsFromToken(authToken)

            val rolesMap: MutableList<String> =
                claims.get("role", MutableList::class.java) as MutableList<String>
            val authorities: MutableList<GrantedAuthority> = ArrayList()
            for (rolemap in rolesMap) {
                authorities.add(SimpleGrantedAuthority(rolemap))
            }
            val user = userRepository.findByEmail(claims.subject)
            if (user != null) {
                Mono.just(
                    UsernamePasswordAuthenticationToken(
                        UserPrincipal.create(user),
                        null,
                        authorities
                    )
                )
            } else Mono.empty()

        } catch (e: Exception) {
            Mono.empty()
        }
    }
}
