package pro.komdosh.tmsc.auth.uaa.config.token

import org.springframework.security.oauth2.jwt.Jwt
import pro.komdosh.tmsc.auth.uaa.user.model.User
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface TokenService {
    fun generateAccessToken(user: User): String
    fun generateRefreshToken(userId: String): Mono<String>
    fun validateAccessToken(token: String): Mono<Boolean>
    fun validateRefreshToken(token: String): Mono<Boolean>
    fun extractUserId(token: String): Mono<String>
    fun extractRoles(token: String): Flux<String>
    fun deactivateRefreshToken(token: String): Mono<Boolean>
    fun decode(token: String): Mono<Jwt>
}
