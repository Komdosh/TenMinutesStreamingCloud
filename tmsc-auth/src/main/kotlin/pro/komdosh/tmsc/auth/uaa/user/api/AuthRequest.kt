package pro.komdosh.tmsc.auth.uaa.user.api

import lombok.NoArgsConstructor
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

@NoArgsConstructor
data class AuthRequest(
    @field:Email
    @field:NotBlank
    var email: String = "",
    @field:NotBlank
    var password: String = ""
) {}
