package pro.komdosh.tmsc.auth.uaa.user

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import pro.komdosh.tmsc.auth.uaa.user.model.User
import java.util.*

@Repository
interface UserRepository : CrudRepository<User, Long> {
    fun findByEmail(email: String): Optional<User>
}
