package pro.komdosh.tmsc.auth.uaa.oauth2


import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import pro.komdosh.tmsc.auth.uaa.user.UserService
import reactor.core.publisher.Mono

@Service
class CustomOAuth2UserService(
    private val userService: UserService
) : DefaultReactiveOAuth2UserService() {


    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(oAuth2UserRequest: OAuth2UserRequest): Mono<OAuth2User> {
        val oAuth2User =
            if (StringUtils.hasText(oAuth2UserRequest.clientRegistration.providerDetails.userInfoEndpoint.uri)) {
                super.loadUser(oAuth2UserRequest)
            } else {
                userService.createUserWithoutUserInfoUri(oAuth2UserRequest.accessToken)
            }

        return oAuth2User.map {
            userService.processOAuth2User(
                oAuth2UserRequest.clientRegistration.registrationId.toUpperCase(),
                it
            )
        }
    }

}
