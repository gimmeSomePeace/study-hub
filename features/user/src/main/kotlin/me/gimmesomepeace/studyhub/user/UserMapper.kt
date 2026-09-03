package me.gimmesomepeace.studyhub.user

import me.gimmesomepeace.studyhub.user.dto.UserDetails
import me.gimmesomepeace.studyhub.user.entity.UserEntity

fun UserEntity.toDetails() = UserDetails(
    id = this.id,
    login = this.login,
    displayName = this.displayName,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)
