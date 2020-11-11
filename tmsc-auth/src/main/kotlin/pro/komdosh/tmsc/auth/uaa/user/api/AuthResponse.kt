package pro.komdosh.tmsc.auth.uaa.user.api

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val type: String = "Bearer"
)
