package me.gimmesomepeace.studyhub.user.auth.exception

import me.gimmesomepeace.studyhub.user.auth.api.AuthController
import me.gimmesomepeace.studyhub.user.exception.LoginAlreadyExistsException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.Instant

@RestControllerAdvice(assignableTypes = [AuthController::class])
class AuthExceptionHandler {
    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentials() = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED).apply {
        detail = "Login or password not correct"
        setProperty("code", "AUTHENTICATION_FAILED")
        setProperty("timestamp", Instant.now())
    }

    @ExceptionHandler(LoginAlreadyExistsException::class)
    fun handleLoginAlreadyExists(e: LoginAlreadyExistsException) = ProblemDetail.forStatus(HttpStatus.CONFLICT).apply {
        detail = "User with login '${e.login}' already exists"
        setProperty("code", "LOGIN_BUSY")
        setProperty("timestamp", Instant.now())
    }
}
