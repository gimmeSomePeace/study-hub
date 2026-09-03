package me.gimmesomepeace.studyhub.user.dto

import java.time.Instant
import java.util.UUID

data class UserDetails(
    val id: UUID,
    val login: String,
    val displayName: String,
    val createdAt: Instant,
    val updatedAt: Instant,
)
