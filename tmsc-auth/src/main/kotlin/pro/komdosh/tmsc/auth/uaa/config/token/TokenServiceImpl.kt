package pro.komdosh.tmsc.auth.uaa.config.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.JWTVerifier
import org.apache.commons.lang3.StringUtils
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import pro.komdosh.tmsc.auth.uaa.config.AppProperties
import pro.komdosh.tmsc.auth.uaa.user.model.User
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.*

@Service
class TokenServiceImpl(
    private val tokenRepository: TokenRepository,
    private val appProperties: AppProperties
) : TokenService {

    private val algorithm: Algorithm = Algorithm.HMAC512(appProperties.auth.tokenSecret)
    private val verifier: JWTVerifier = JWT.require(algorithm).build()

    override fun generateAccessToken(user: User): String {
        val creationDate = Instant.now()

        return JWT.create()
            .withSubject(user.email)
            .withIssuedAt(Date.from(creationDate))
            .withExpiresAt(Date.from(creationDate.plusSeconds(appProperties.auth.tokenExpirationMsec)))
            .withArrayClaim(ROLES_CLAIM, user.roles.map { it.name }.toTypedArray())
            .sign(algorithm)
    }

    override fun generateRefreshToken(userId: String): Mono<String> {
        return Mono.just(Instant.now())
            .map { creationDate: Instant ->
                JWT.create()
                    .withSubject(userId)
                    .withIssuedAt(Date.from(creationDate))
                    .withExpiresAt(
                        Date.from(
                            creationDate.plusSeconds(
                                appProperties.auth.refreshTokenExpirationMsec
                            )
                        )
                    )
                    .sign(algorithm)
            }
            .map { token: String? ->
                RefreshToken(
                    token!!, userId, java.lang.Boolean.TRUE
                )
            }
            .map { token -> tokenRepository.save(token) }
            .map(RefreshToken::token)
    }

    override fun validateAccessToken(token: String): Mono<Boolean> {
        return Mono.just(token)
            .filter { StringUtils.isNotBlank(token) }
            .map { token -> decodeToken(token) }
            .map { t -> t.expiresAt }
            .map { expirationDate -> expirationDate.after(Date.from(Instant.now())) }
    }

    override fun validateRefreshToken(token: String): Mono<Boolean> {
        return Mono.just(token)
            .map { tokenRepository.findById(it) }
            .map { it.map { token -> token?.isActive }.orElse(false) }
    }

    override fun extractUserId(token: String): Mono<String> {
        return Mono.just(token)
            .map { token -> decodeToken(token) }
            .map { decodedJWT -> decodedJWT.subject }
    }

    override fun extractRoles(token: String): Flux<String> {
        return Mono.just(token)
            .map { token -> decodeToken(token) }
            .map { decodedJWT -> decodedJWT.getClaim(ROLES_CLAIM) }
            .filter { claim: Claim -> !claim.isNull }
            .flatMapIterable { claim: Claim ->
                claim.asList(
                    String::class.java
                )
            }
    }

    override fun deactivateRefreshToken(token: String): Mono<Boolean> {
        return Mono.justOrEmpty(tokenRepository.findById(token))
            .doOnNext { refreshToken -> refreshToken.isActive = false }
            .map { token -> tokenRepository.save(token) }
            .map { refreshToken: RefreshToken -> !refreshToken.isActive }
    }

    private fun decodeToken(token: String): DecodedJWT {
        return verifier.verify(token)
    }

    override fun decode(token: String): Mono<Jwt> {
        return Mono.just(token).map {
            verifier.verify(it)
        }.map { jwt ->
            Jwt.withTokenValue(jwt.token).claims {
                it.putAll(jwt.claims)
            }.build()
        }
    }

    companion object {
        private const val ROLES_CLAIM = "roles"
    }
}
