package me.gimmesomepeace.studyhub.core.token

import me.gimmesomepeace.studyhub.core.user.UserRole
import java.util.UUID

data class ParsedToken(
    val userId: UUID,
    val role: UserRole,
)
