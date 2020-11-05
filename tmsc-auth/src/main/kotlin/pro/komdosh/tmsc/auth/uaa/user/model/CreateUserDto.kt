package pro.komdosh.tmsc.auth.uaa.user.model

import lombok.NoArgsConstructor
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

@NoArgsConstructor
data class CreateUserDto(
    @field:NotBlank(message = "Email is mandatory")
    @field:Email(message = "Invalid mailbox")
    val email: String?,
    @field:NotBlank(message = "Password is mandatory")
    var password: String?,
    val name: String?,
    val photoUrl: String?
)
