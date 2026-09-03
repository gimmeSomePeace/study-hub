package me.gimmesomepeace.studyhub.user.repository

import me.gimmesomepeace.studyhub.user.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository : JpaRepository<UserEntity, UUID> {
    fun findByLogin(login: String): UserEntity?

    fun existsByLogin(login: String): Boolean
}
