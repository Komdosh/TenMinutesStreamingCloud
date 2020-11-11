package pro.komdosh.tmsc.auth.uaa.user


import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.user.OAuth2User
import pro.komdosh.tmsc.auth.uaa.config.token.TokenRequest
import pro.komdosh.tmsc.auth.uaa.user.api.AuthRequest
import pro.komdosh.tmsc.auth.uaa.user.api.AuthResponse
import pro.komdosh.tmsc.auth.uaa.user.model.CreateUserDto
import pro.komdosh.tmsc.auth.uaa.user.model.User
import pro.komdosh.tmsc.auth.uaa.user.model.UserDto
import reactor.core.publisher.Mono


interface UserService {
    fun getUserByEmail(email: String): Mono<User>
    fun authenticate(authRequest: AuthRequest): Mono<AuthResponse>
    fun create(createDto: CreateUserDto): Mono<UserDto>
    fun getCurrentUser(): Mono<User>
    fun createUserWithoutUserInfoUri(oauth2AccessToken: OAuth2AccessToken): Mono<OAuth2User>
    fun processOAuth2User(
        clientRegistration: String,
        oAuth2User: OAuth2User
    ): OAuth2User

    fun refreshAccessToken(request: TokenRequest): Mono<String>
    fun invalidateRefreshToken(request: TokenRequest): Mono<Boolean>
}
