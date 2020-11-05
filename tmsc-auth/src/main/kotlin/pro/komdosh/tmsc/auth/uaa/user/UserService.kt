package pro.komdosh.tmsc.auth.uaa.user


import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.user.OAuth2User
import reactor.core.publisher.Mono


interface UserService {
    fun getUserByEmail(email: String): Mono<User>
    fun create(createDto: CreateUserDto): Mono<UserDto>
    fun getCurrentUser(): Mono<User>
    fun createUserWithoutUserInfoUri(oauth2AccessToken: OAuth2AccessToken): Mono<OAuth2User>
    fun processOAuth2User(
        clientRegistration: String,
        oAuth2User: OAuth2User
    ): OAuth2User
}
