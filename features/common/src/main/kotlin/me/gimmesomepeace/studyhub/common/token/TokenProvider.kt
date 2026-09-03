package me.gimmesomepeace.studyhub.common.token

import me.gimmesomepeace.studyhub.common.user.UserRole
import java.util.UUID

interface TokenProvider {
    fun generateToken(
        userId: UUID,
        role: UserRole,
    ): AccessToken

    fun parseToken(token: String): ParsedToken
}
