package me.gimmesomepeace.studyhub.user.auth.service

import jakarta.validation.ValidationException
import me.gimmesomepeace.studyhub.common.id.IdGenerator
import me.gimmesomepeace.studyhub.common.token.AccessToken
import me.gimmesomepeace.studyhub.common.token.TokenProvider
import me.gimmesomepeace.studyhub.user.auth.api.login.LoginRequest
import me.gimmesomepeace.studyhub.user.auth.api.reg.RegisterRequest
import me.gimmesomepeace.studyhub.user.auth.exception.InvalidCredentialsException
import me.gimmesomepeace.studyhub.user.dto.UserDetails
import me.gimmesomepeace.studyhub.user.entity.UserEntity
import me.gimmesomepeace.studyhub.user.exception.LoginAlreadyExistsException
import me.gimmesomepeace.studyhub.user.repository.UserRepository
import me.gimmesomepeace.studyhub.user.toDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AuthService(
    private val tokenProvider: TokenProvider,
    private val passwordEncoder: PasswordEncoder,
    private val userRepository: UserRepository,
    private val idGenerator: IdGenerator<UUID>,
) {
    fun login(request: LoginRequest): AccessToken {
        val user = userRepository.findByLogin(request.login) ?: throw InvalidCredentialsException()
        if (!passwordEncoder.matches(request.password, user.passwordHash)) throw InvalidCredentialsException()
        return tokenProvider.generateToken(user.id, user.role)
    }

    fun register(request: RegisterRequest): UserDetails {
        if (userRepository.existsByLogin(request.login)) throw LoginAlreadyExistsException(request.login)
        val user = UserEntity(
            id = idGenerator.generate(),
            login = request.login,
            passwordHash = passwordEncoder.encode(request.password) ?: throw ValidationException("password is empty"),
            displayName = request.displayName,
        )
        userRepository.save(user)
        return user.toDetails()
    }
}
