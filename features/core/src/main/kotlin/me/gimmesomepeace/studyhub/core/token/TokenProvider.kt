package me.gimmesomepeace.studyhub.core.token

import me.gimmesomepeace.studyhub.core.user.UserRole
import java.util.UUID

interface TokenProvider {
    fun generateToken(
        userId: UUID,
        role: UserRole,
    ): AccessToken

    fun parseToken(token: String): ParsedToken
}
