package me.gimmesomepeace.studyhub.user.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import me.gimmesomepeace.studyhub.user.UserConstraints
import me.gimmesomepeace.studyhub.common.user.UserRole
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "app_users")
data class UserEntity(
    @Id
    val id: UUID,

    @Column(nullable = false, unique = true, length = UserConstraints.MAX_LOGIN_LENGTH)
    val login: String,

    @Column(name = "password_hash", nullable = false)
    val passwordHash: String,

    @Column(name = "display_name", nullable = false, length = UserConstraints.MAX_DISPLAY_NAME_LENGTH)
    val displayName: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val role: UserRole = UserConstraints.DEFAULT_USER_ROLE,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),
)
