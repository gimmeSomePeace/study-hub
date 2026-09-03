package me.gimmesomepeace.studyhub.user.auth.api

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import me.gimmesomepeace.studyhub.user.auth.exception.InvalidCredentialsException
import me.gimmesomepeace.studyhub.user.auth.fixtures.loginRequest
import me.gimmesomepeace.studyhub.user.auth.fixtures.registerRequest
import me.gimmesomepeace.studyhub.user.auth.service.AuthService
import me.gimmesomepeace.studyhub.user.exception.LoginAlreadyExistsException
import me.gimmesomepeace.studyhub.user.fixtures.login
import me.gimmesomepeace.studyhub.user.fixtures.token
import me.gimmesomepeace.studyhub.user.fixtures.userDetails
import me.gimmesomepeace.studyhub.user.fixtures.userId
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import tools.jackson.databind.ObjectMapper

@WebMvcTest(AuthController::class)
class AuthControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var authService: AuthService

    private val login = login()

    @Nested
    inner class Login {
        @Test
        fun `should return token when credentials are valid`() {
            val request = loginRequest(login = login)
            val expectedToken = token()

            every { authService.login(request) } returns expectedToken

            mvc
                .post("/auth/login") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.accessToken") { value(expectedToken) }
                }
        }

        @Test
        fun `should return 401 when authentication fails`() {
            val request = loginRequest(login = login)

            every { authService.login(request) } throws InvalidCredentialsException()

            mvc
                .post("/auth/login") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isUnauthorized() }
                    jsonPath("$.code") { value("AUTHENTICATION_FAILED") }
                }

            verify { authService.login(request) }
        }

        @ParameterizedTest
        @MethodSource(
            "me.gimmesomepeace.studyhub.user.auth.fixtures.AuthTestData#invalidLoginRequests",
        )
        fun `should return 400 when request is invalid`(requestBody: Map<String, Any?>) {
            mvc
                .post("/auth/login") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(requestBody)
                }.andExpect {
                    status { isBadRequest() }
                }
        }
    }

    @Nested
    inner class Register {
        @Test
        fun `should create user and return details`() {
            val request = registerRequest(
                login = login,
                displayName = "Test User",
            )

            val created = userDetails(
                id = userId(),
                login = request.login,
                displayName = request.displayName,
            )

            every { authService.register(request) } returns created

            mvc
                .post("/auth/register") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.id") { value(created.id.toString()) }
                    jsonPath("$.login") { value(request.login) }
                    jsonPath("$.displayName") { value(request.displayName) }
                    jsonPath("$.createdAt") { exists() }
                    jsonPath("$.updatedAt") { exists() }
                }
        }

        @Test
        fun `should return 409 when login is already taken`() {
            val request = registerRequest(login = login)

            every { authService.register(request) } throws LoginAlreadyExistsException(login)

            mvc
                .post("/auth/register") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isConflict() }
                    jsonPath("$.code") { value("LOGIN_BUSY") }
                }
        }

        @ParameterizedTest
        @MethodSource(
            "me.gimmesomepeace.studyhub.user.auth.fixtures.AuthTestData#invalidRegisterRequests",
        )
        fun `should return 400 when request is invalid`(requestBody: Map<String, Any?>) {
            mvc
                .post("/auth/register") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(requestBody)
                }.andExpect {
                    status { isBadRequest() }
                }
        }
    }
}
