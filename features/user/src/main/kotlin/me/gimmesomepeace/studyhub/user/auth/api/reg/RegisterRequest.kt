package me.gimmesomepeace.studyhub.user.auth.api.reg

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import me.gimmesomepeace.studyhub.user.UserConstraints.MAX_DISPLAY_NAME_LENGTH
import me.gimmesomepeace.studyhub.user.UserConstraints.MAX_LOGIN_LENGTH

data class RegisterRequest(
    @field:NotBlank
    @field:Size(max = MAX_LOGIN_LENGTH)
    val login: String,

    @field:NotBlank
    @field:Size(max = MAX_DISPLAY_NAME_LENGTH)
    val displayName: String,

    @field:NotBlank
    val password: String,
)
