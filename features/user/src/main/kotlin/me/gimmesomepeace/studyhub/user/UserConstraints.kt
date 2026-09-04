package me.gimmesomepeace.studyhub.user

import me.gimmesomepeace.studyhub.core.user.UserRole

object UserConstraints {
    const val MAX_LOGIN_LENGTH = 100
    const val MAX_DISPLAY_NAME_LENGTH = 255

    val DEFAULT_USER_ROLE = UserRole.USER
}
