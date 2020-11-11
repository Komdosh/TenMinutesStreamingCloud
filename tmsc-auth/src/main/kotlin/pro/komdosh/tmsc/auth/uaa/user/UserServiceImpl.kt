package pro.komdosh.tmsc.auth.uaa.user

import javassist.NotFoundException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import pro.komdosh.tmsc.auth.log
import pro.komdosh.tmsc.auth.uaa.config.token.TokenRequest
import pro.komdosh.tmsc.auth.uaa.config.token.TokenService
import pro.komdosh.tmsc.auth.uaa.oauth2.OAuth2UserInfo
import pro.komdosh.tmsc.auth.uaa.oauth2.OAuth2UserInfoFactory
import pro.komdosh.tmsc.auth.uaa.user.api.AuthRequest
import pro.komdosh.tmsc.auth.uaa.user.api.AuthResponse
import pro.komdosh.tmsc.auth.uaa.user.model.*
import reactor.core.publisher.Mono
import java.security.Principal
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashSet


@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenService: TokenService
) : UserService {

    override fun create(createDto: CreateUserDto): Mono<UserDto> {
        log.debug { "Attempting to create $createDto" }

        return Mono.just(userRepository.save(User.fromDto(createDto, passwordEncoder))).map {
            log.debug { "User $it saved successfully" }
            it.toDto()
        }
    }

    override fun getUserByEmail(email: String): Mono<User> {
        log.debug { "Attempting to fetch user with email $email" }
        return Mono.justOrEmpty(userRepository.findByEmail(email)).map {
            log.debug { "Fetched user: $it" }
            it
        }.switchIfEmpty(
            Mono.error(NotFoundException("User with provided email doesn't exists"))
        )
    }

    override fun getCurrentUser(): Mono<User> {
        return ReactiveSecurityContextHolder.getContext().map { it.authentication as Principal }
            .filter { principal -> principal == null || !(principal as Authentication).isAuthenticated }
            .flatMap { principal -> getUserByEmail(principal.name) }
            .switchIfEmpty(Mono.error(AccessDeniedException("There is not authenticated user")))
    }

    override fun createUserWithoutUserInfoUri(oauth2AccessToken: OAuth2AccessToken): Mono<OAuth2User> {
        val userNameAttributeName = "name"
        val userAttributes: MutableMap<String, Any> = HashMap()

        val tempId = "sandbox" + UUID.randomUUID()
        userAttributes["id"] = tempId
        userAttributes["name"] = "sandbox"
        userAttributes["email"] = tempId
        userAttributes["accessToken"] = oauth2AccessToken.tokenValue

        val authorities: MutableSet<GrantedAuthority> = LinkedHashSet()
        authorities.add(OAuth2UserAuthority(userAttributes))
        for (authority in oauth2AccessToken.scopes) {
            authorities.add(SimpleGrantedAuthority("SCOPE_$authority"))
        }

        return Mono.just(DefaultOAuth2User(authorities, userAttributes, userNameAttributeName))
    }

    override fun processOAuth2User(
        clientRegistration: String,
        oAuth2User: OAuth2User
    ): OAuth2User {

        val oAuth2UserInfo: OAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
            clientRegistration,
            oAuth2User.attributes
        )
        if (StringUtils.isEmpty(oAuth2UserInfo.email)) {
            throw IllegalStateException("Email not found from OAuth2 provider")
        }
        val user = userRepository.findByEmail(oAuth2UserInfo.email).map {
            if (it.provider != AuthProvider.valueOf(clientRegistration)) {
                throw IllegalStateException(
                    "Looks like you're signed up" +
                            " with ${it.provider} account. Please use your ${it.provider} account to login."
                )
            }
            updateExistingUser(it, oAuth2UserInfo)
        }.orElseGet { registerNewUser(clientRegistration, oAuth2UserInfo) }

        return UserPrincipal.create(user, oAuth2User.attributes)

    }

    private fun registerNewUser(
        clientRegistration: String,
        oAuth2UserInfo: OAuth2UserInfo
    ): User {
        val user = User(
            null,
            oAuth2UserInfo.email,
            "",
            oAuth2UserInfo.name,
            oAuth2UserInfo.imageUrl,
            setOf(UserRole.ROLE_USER),
            AuthProvider.valueOf(clientRegistration),
            oAuth2UserInfo.id
        )

        return userRepository.save(user)
    }

    private fun updateExistingUser(existingUser: User, oAuth2UserInfo: OAuth2UserInfo): User {
        return userRepository.save(
            User.fromDto(UpdateUserDto(oAuth2UserInfo), existingUser, passwordEncoder)
        )
    }

    override fun refreshAccessToken(request: TokenRequest): Mono<String> {
        return Mono.just(request)
            .map(TokenRequest::refreshToken)
            .filterWhen(tokenService::validateRefreshToken)
            .switchIfEmpty(Mono.error(RuntimeException("Token is not valid")))
            .flatMap(tokenService::extractUserId)
            .flatMap { Mono.justOrEmpty(userRepository.findByEmail(it)) }
            .switchIfEmpty(Mono.error(RuntimeException("User not found")))
            .map(tokenService::generateAccessToken)
    }

    override fun invalidateRefreshToken(request: TokenRequest): Mono<Boolean> {
        return Mono.just(request)
            .map(TokenRequest::refreshToken)
            .filterWhen(tokenService::validateRefreshToken)
            .switchIfEmpty(Mono.error(RuntimeException("Token is not valid")))
            .flatMap(tokenService::deactivateRefreshToken)
    }

    override fun authenticate(authRequest: AuthRequest): Mono<AuthResponse> {
        return Mono.justOrEmpty(userRepository.findByEmail(authRequest.email).map { it })
            .switchIfEmpty(Mono.error(RuntimeException("User not found")))
            .filter(User::enabled)
            .switchIfEmpty(Mono.error(RuntimeException("User not enabled")))
            .filter { user -> passwordEncoder.matches(authRequest.password, user.password) }
            .switchIfEmpty(Mono.error(RuntimeException("Wrong password")))
            .flatMap { user ->
                Mono.zip(
                    Mono.just(tokenService.generateAccessToken(user)),
                    tokenService.generateRefreshToken(user.email)
                )
            }
            .map { tokens -> AuthResponse(tokens.t1, tokens.t2) }
    }
}
