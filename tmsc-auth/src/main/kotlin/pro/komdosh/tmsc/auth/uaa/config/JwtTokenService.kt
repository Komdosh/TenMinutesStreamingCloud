package pro.komdosh.tmsc.auth.uaa.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import pro.komdosh.tmsc.auth.uaa.user.User
import java.security.Key
import java.util.*

@Service
class JwtTokenService(
    private val appProperties: AppProperties
) {

    private var key: Key = Keys.hmacShaKeyFor(appProperties.auth.tokenSecret?.toByteArray())

    fun getAllClaimsFromToken(token: String?): Claims {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body
    }

    fun getUsernameFromToken(token: String?): String {
        return getAllClaimsFromToken(token).subject
    }

    fun getExpirationDateFromToken(token: String?): Date {
        return getAllClaimsFromToken(token).expiration
    }

    private fun isTokenExpired(token: String): Boolean {
        val expiration = getExpirationDateFromToken(token)
        return expiration.before(Date())
    }

    fun generateToken(user: User): String {
        val claims: MutableMap<String, Any?> = HashMap()
        claims["role"] = user.roles
        return doGenerateToken(claims, user.email)
    }

    private fun doGenerateToken(claims: Map<String, Any?>, username: String): String {
        val createdDate = Date()
        val expirationDate = Date(createdDate.time + appProperties.auth.tokenExpirationMsec)
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(username)
            .setIssuedAt(createdDate)
            .setExpiration(expirationDate)
            .signWith(key)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return !isTokenExpired(token)
    }
}
