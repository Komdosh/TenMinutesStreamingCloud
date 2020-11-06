package pro.komdosh.tmsc.auth.uaa.user.model

import lombok.NoArgsConstructor
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import pro.komdosh.tmsc.auth.uaa.oauth2.OAuth2UserInfo
import pro.komdosh.tmsc.auth.uaa.user.AuthProvider
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

@Entity
@Table(
        name = "users", uniqueConstraints = [
                UniqueConstraint(columnNames = ["email"])
        ]
)
data class User(
        @Id @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long? = null,
        val email: String = "",
        val password: String = "",
        val name: String = "",
        val photoUrl: String = "",
        @Enumerated(EnumType.STRING)
        @ElementCollection(fetch = FetchType.EAGER)
        val roles: Set<UserRole> = setOf(UserRole.ROLE_USER),
        @Enumerated(EnumType.STRING)
        val provider: AuthProvider = AuthProvider.LOCAL,
        val providerId: String = ""
) {
        fun toDto(): UserDto = UserDto(
                id = this.id ?: 0,
                email = this.email,
                name = this.name,
                photoUrl = this.photoUrl,
                roles = this.roles,
                provider = this.provider
        )

        companion object {
                fun fromDto(
                        dto: CreateUserDto,
                        passwordEncoder: PasswordEncoder = BCryptPasswordEncoder(),
                        authProvider: AuthProvider = AuthProvider.LOCAL
                ) = User(
                        id = null,
                        email = dto.email ?: "",
                        password = passwordEncoder.encode(dto.password ?: ""),
                        name = dto.name ?: "",
                        photoUrl = dto.photoUrl ?: "",
                        roles = setOf(UserRole.ROLE_USER),
                        provider = authProvider
                )

                fun fromDto(
                        dto: UpdateUserDto,
                        entity: User,
                        passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()
                ) = User(
                        id = entity.id,
                        email = dto.email ?: entity.email,
                        password = passwordEncoder.encode(dto.password ?: entity.password),
                        name = dto.name ?: entity.name,
                        photoUrl = dto.photoUrl ?: entity.photoUrl,
                        roles = entity.roles,
                        provider = entity.provider
                )
        }

}


data class UserDto(
        val id: Long,
        val email: String,
        val roles: Set<UserRole>,
        val name: String,
        val photoUrl: String,
        val provider: AuthProvider
)

@NoArgsConstructor
data class UpdateUserDto(
        @field:Email(message = "Invalid mailbox")
        val email: String?,
        val password: String?,
        val name: String?,
        val photoUrl: String?
) {
        constructor(oauth2User: OAuth2UserInfo) : this(
                null,
                null,
                oauth2User.name,
                oauth2User.imageUrl
        )
}

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
