package pro.komdosh.tmsc.auth.uaa.config.token

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TokenRepository : CrudRepository<RefreshToken, String> {
}
