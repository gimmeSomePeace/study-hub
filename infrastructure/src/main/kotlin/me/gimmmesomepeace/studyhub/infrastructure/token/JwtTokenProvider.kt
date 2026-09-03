package me.gimmmesomepeace.studyhub.infrastructure.token

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import me.gimmesomepeace.studyhub.common.token.AccessToken
import me.gimmesomepeace.studyhub.common.token.ParsedToken
import me.gimmesomepeace.studyhub.common.token.TokenProvider
import me.gimmesomepeace.studyhub.common.user.UserRole
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey

class JwtTokenProvider(
    secret: String,
    private val expirationMs: Long,
) : TokenProvider {
    private val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

    override fun generateToken(
        userId: UUID,
        role: UserRole,
    ): AccessToken {
        val now = Date()
        val expiry = Date(now.time + expirationMs)

        val token = Jwts
            .builder()
            .subject(userId.toString())
            .claim("role", role.name)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
            .compact()
        return AccessToken(token)
    }

    override fun parseToken(token: String): ParsedToken {
        val claims = Jwts
            .parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload

        val userId = UUID.fromString(claims.subject)
        val role = UserRole.valueOf(claims["role"] as String)
        return ParsedToken(userId, role)
    }
}
