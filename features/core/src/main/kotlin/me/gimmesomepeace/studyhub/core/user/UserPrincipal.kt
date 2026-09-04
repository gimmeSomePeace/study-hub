package me.gimmesomepeace.studyhub.core.user

import java.util.UUID

data class UserPrincipal(
    val userId: UUID,
    val role: UserRole,
)
