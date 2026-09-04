package me.gimmesomepeace.studyhub.user.auth.api

import jakarta.validation.Valid
import me.gimmesomepeace.studyhub.user.auth.api.login.LoginRequest
import me.gimmesomepeace.studyhub.user.auth.api.login.LoginResult
import me.gimmesomepeace.studyhub.user.auth.api.reg.RegisterRequest
import me.gimmesomepeace.studyhub.user.auth.service.AuthService
import me.gimmesomepeace.studyhub.user.dto.UserDetails
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest,
    ): ResponseEntity<LoginResult> {
        val token = authService.login(request)
        val result = LoginResult(token.value)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/register")
    fun register(
        @Valid @RequestBody request: RegisterRequest,
    ): ResponseEntity<UserDetails> {
        val user = authService.register(request)
        return ResponseEntity.ok(user)
    }
}
