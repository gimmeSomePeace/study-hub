package me.gimmesomepeace.studyhub.user.exception

class LoginAlreadyExistsException(
    val login: String,
) : RuntimeException("User with '$login' already exists")
