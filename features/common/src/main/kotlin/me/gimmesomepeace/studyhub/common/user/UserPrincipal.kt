package me.gimmesomepeace.studyhub.common.user

import java.util.UUID

data class UserPrincipal(
    val userId: UUID,
    val role: UserRole,
)
