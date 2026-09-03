package me.gimmesomepeace.studyhub.common.token

import me.gimmesomepeace.studyhub.common.user.UserRole
import java.util.UUID

data class ParsedToken(
    val userId: UUID,
    val role: UserRole,
)
