package me.gimmesomepeace.studyhub.user.auth.fixtures

import me.gimmesomepeace.studyhub.user.auth.api.login.LoginRequest
import me.gimmesomepeace.studyhub.user.auth.api.reg.RegisterRequest
import me.gimmesomepeace.studyhub.user.fixtures.displayName
import me.gimmesomepeace.studyhub.user.fixtures.login
import me.gimmesomepeace.studyhub.user.fixtures.password


fun loginRequest(
    login: String = login(),
    password: String = password(),
) = LoginRequest(
    login = login,
    password = password,
)

fun registerRequest(
    login: String = login(),
    displayName: String = displayName(),
    password: String = password(),
) = RegisterRequest(
    login = login,
    displayName = displayName,
    password = password,
)
