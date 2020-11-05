package pro.komdosh.tmsc.auth.uaa.oauth2

import pro.komdosh.tmsc.auth.uaa.user.AuthProvider


class FacebookOAuth2UserInfo(attributes: Map<String, Any>) : OAuth2UserInfo(attributes) {

    override val id: String
        get() = attributes["id"] as String? ?: ""

    override val name: String
        get() = attributes["name"] as String? ?: ""

    override val email: String
        get() = attributes["email"] as String? ?: ""

    override val imageUrl: String
        get(): String {
            if (attributes.containsKey("picture")) {
                val pictureObj = attributes["picture"] as Map<String, Any>
                if (pictureObj.containsKey("data")) {
                    val dataObj = pictureObj["data"] as Map<String, Any>
                    if (dataObj.containsKey("url")) {
                        return dataObj["url"] as String
                    }
                }
            }
            return ""
        }
}

class GoogleOAuth2UserInfo(attributes: Map<String, Any>) : OAuth2UserInfo(attributes) {
    override val id: String
        get() = attributes["sub"] as String? ?: ""

    override val name: String
        get() = attributes["name"] as String? ?: ""

    override val email: String
        get() = attributes["email"] as String? ?: ""

    override val imageUrl: String
        get() = attributes["picture"] as String? ?: ""
}

class LocalOAuth2UserInfo(attributes: Map<String, Any>) : OAuth2UserInfo(attributes) {
    override val id: String
        get() = attributes["id"] as String? ?: ""
    override val name: String
        get() = attributes["name"] as String? ?: ""
    override val email: String
        get() = attributes["email"] as String? ?: ""
    override val imageUrl: String
        get() = attributes["imageUrl"] as String? ?: ""
    val accessToken: String
        get() = attributes["accessToken"] as String? ?: ""
}


abstract class OAuth2UserInfo(var attributes: Map<String, Any>) {
    abstract val id: String
    abstract val name: String
    abstract val email: String
    abstract val imageUrl: String
}

object OAuth2UserInfoFactory {
    @JvmStatic
    fun getOAuth2UserInfo(registrationId: String, attributes: Map<String, Any>): OAuth2UserInfo {
        return when {
            registrationId.equals(AuthProvider.GOOGLE.toString(), ignoreCase = true) -> {
                GoogleOAuth2UserInfo(attributes)
            }
            registrationId.equals(AuthProvider.FACEBOOK.toString(), ignoreCase = true) -> {
                FacebookOAuth2UserInfo(attributes)
            }
            registrationId.equals(AuthProvider.LOCAL.toString(), ignoreCase = true) -> {
                LocalOAuth2UserInfo(attributes)
            }
            else -> {
                throw IllegalArgumentException("Sorry! Login with $registrationId is not supported yet.")
            }
        }
    }
}
