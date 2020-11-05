package pro.komdosh.tmsc.auth.uaa.user.api

data class AuthResponse(
    val accessToken: String,
    val tokenType: String = "Bearer",
    val full: String = "$tokenType $accessToken"
)
