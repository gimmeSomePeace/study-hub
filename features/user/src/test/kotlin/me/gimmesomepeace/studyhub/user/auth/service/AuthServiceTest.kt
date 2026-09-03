package me.gimmesomepeace.studyhub.user.auth.service

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import me.gimmesomepeace.studyhub.common.id.IdGenerator
import me.gimmesomepeace.studyhub.common.token.TokenProvider
import me.gimmesomepeace.studyhub.common.user.UserRole
import me.gimmesomepeace.studyhub.user.auth.exception.InvalidCredentialsException
import me.gimmesomepeace.studyhub.user.auth.fixtures.loginRequest
import me.gimmesomepeace.studyhub.user.auth.fixtures.registerRequest
import me.gimmesomepeace.studyhub.user.entity.UserEntity
import me.gimmesomepeace.studyhub.user.exception.LoginAlreadyExistsException
import me.gimmesomepeace.studyhub.user.fixtures.login
import me.gimmesomepeace.studyhub.user.fixtures.password
import me.gimmesomepeace.studyhub.user.fixtures.passwordHash
import me.gimmesomepeace.studyhub.user.fixtures.token
import me.gimmesomepeace.studyhub.user.fixtures.userEntity
import me.gimmesomepeace.studyhub.user.fixtures.userId
import me.gimmesomepeace.studyhub.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.UUID

@ExtendWith(MockKExtension::class)
class AuthServiceTest {
    @MockK
    private lateinit var tokenProvider: TokenProvider

    @MockK
    private lateinit var passwordEncoder: PasswordEncoder

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var idGenerator: IdGenerator<UUID>

    private lateinit var service: AuthService

    private val userId = userId()
    private val login = login()
    private val password = password()

    @BeforeEach
    fun setUp() {
        service = AuthService(
            tokenProvider = tokenProvider,
            passwordEncoder = passwordEncoder,
            userRepository = userRepository,
            idGenerator = idGenerator,
        )
    }

    @Nested
    inner class Login {
        @Test
        fun `should return token when credentials are valid`() {
            val user = userEntity(
                id = userId,
                login = login,
                passwordHash = passwordHash(),
            )
            val request = loginRequest(login = login, password = password)
            val expectedToken = token()

            every { userRepository.findByLogin(login) } returns user
            every { passwordEncoder.matches(password, user.passwordHash) } returns true
            every { tokenProvider.generateToken(userId, UserRole.USER) } returns expectedToken

            val result = service.login(request)

            assertThat(result).isEqualTo(expectedToken)
            verify { tokenProvider.generateToken(userId, user.role) }
        }

        @Test
        fun `should throw InvalidCredentialsException when user not found`() {
            val request = loginRequest(login = login, password = password)

            every { userRepository.findByLogin(login) } returns null

            assertThatThrownBy { service.login(request) }
                .isInstanceOf(InvalidCredentialsException::class.java)
        }

        @Test
        fun `should throw InvalidCredentialsException when password is wrong`() {
            val user = userEntity(
                id = userId,
                login = login,
                passwordHash = passwordHash(),
            )
            val request = loginRequest(login = login, password = "wrongpassword")

            every { userRepository.findByLogin(login) } returns user
            every { passwordEncoder.matches("wrongpassword", user.passwordHash) } returns false

            assertThatThrownBy { service.login(request) }
                .isInstanceOf(InvalidCredentialsException::class.java)
        }
    }

    @Nested
    inner class Register {
        @Test
        fun `should create user and return details`() {
            val request = registerRequest(
                login = login,
                displayName = "Test User",
                password = password,
            )

            val generatedId = userId()

            every { userRepository.existsByLogin(login) } returns false
            every { idGenerator.generate() } returns generatedId
            every { passwordEncoder.encode(password) } returns passwordHash()
            every { userRepository.save(any()) } answers { firstArg() }

            val result = service.register(request)

            assertThat(result.id).isEqualTo(generatedId)
            assertThat(result.login).isEqualTo(request.login)
            assertThat(result.displayName).isEqualTo(request.displayName)

            verify { idGenerator.generate() }
            verify { userRepository.save(any()) }
        }

        @Test
        fun `should throw LoginAlreadyExistsException when login is taken`() {
            val request = registerRequest(login = login)

            every { userRepository.existsByLogin(login) } returns true

            assertThatThrownBy { service.register(request) }
                .isInstanceOf(LoginAlreadyExistsException::class.java)
                .hasMessageContaining(login)

            verify(exactly = 0) { idGenerator.generate() }
            verify(exactly = 0) { userRepository.save(any()) }
        }
    }
}
