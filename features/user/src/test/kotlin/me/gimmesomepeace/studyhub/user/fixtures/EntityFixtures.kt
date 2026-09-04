package me.gimmesomepeace.studyhub.user.fixtures

import me.gimmesomepeace.studyhub.core.token.AccessToken
import me.gimmesomepeace.studyhub.core.user.UserRole
import me.gimmesomepeace.studyhub.user.entity.UserEntity
import java.time.Instant
import java.util.UUID

fun userId(): UUID = UUID.randomUUID()

fun login(): String = "testuser"

fun displayName(): String = "Test User"

fun password(): String = "password123"

fun passwordHash(): String = $$"$2a$10$hashedpassword"

fun token(): AccessToken = AccessToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.signature")

fun userEntity(
    id: UUID = userId(),
    login: String = login(),
    passwordHash: String = passwordHash(),
    displayName: String = displayName(),
    role: UserRole = UserRole.USER,
    createdAt: Instant = Instant.parse("2026-01-01T00:00:00Z"),
    updatedAt: Instant = Instant.parse("2026-01-01T00:00:00Z"),
) = UserEntity(
    id = id,
    login = login,
    passwordHash = passwordHash,
    displayName = displayName,
    role = role,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
