package pro.komdosh.tmsc.auth.uaa.config.token

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class RefreshToken(
    @Id val token: String = "",
    val userId: String = "",
    var isActive: Boolean = true
) {}
