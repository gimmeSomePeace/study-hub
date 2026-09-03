package me.gimmesomepeace.studyhub.user.fixtures

import me.gimmesomepeace.studyhub.user.dto.UserDetails
import java.time.Instant
import java.util.UUID

fun userDetails(
    id: UUID = userId(),
    login: String = login(),
    displayName: String = displayName(),
    createdAt: Instant = Instant.parse("2026-01-01T00:00:00Z"),
    updatedAt: Instant = Instant.parse("2026-01-01T00:00:00Z"),
) = UserDetails(
    id = id,
    login = login,
    displayName = displayName,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
