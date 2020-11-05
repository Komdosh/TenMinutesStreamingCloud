package pro.komdosh.tmsc.auth.uaa.user.api

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class AuthRequest(
    @Email
    @NotBlank
    val email: String,
    @NotBlank
    val password: String
)
