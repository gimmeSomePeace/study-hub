package me.gimmesomepeace.studyhub.user.auth.fixtures

import org.junit.jupiter.params.provider.Arguments
import java.util.stream.Stream

@Suppress("unused")
object AuthTestData {
    @JvmStatic
    fun invalidLoginRequests(): Stream<Arguments> = Stream.of(
        Arguments.of(loginRequestMap(login = null)),
        Arguments.of(loginRequestMap(login = "")),
        Arguments.of(loginRequestMap(login = "   ")),
        Arguments.of(loginRequestMap(password = null)),
        Arguments.of(loginRequestMap(password = "")),
        Arguments.of(loginRequestMap(password = "   ")),
    )

    @JvmStatic
    fun invalidRegisterRequests(): Stream<Arguments> = Stream.of(
        Arguments.of(registerRequestMap(login = null)),
        Arguments.of(registerRequestMap(login = "")),
        Arguments.of(registerRequestMap(login = "   ")),
        Arguments.of(registerRequestMap(login = "a".repeat(300))),
        Arguments.of(registerRequestMap(password = null)),
        Arguments.of(registerRequestMap(password = "")),
        Arguments.of(registerRequestMap(displayName = null)),
        Arguments.of(registerRequestMap(displayName = "")),
        Arguments.of(registerRequestMap(displayName = "   ")),
        Arguments.of(registerRequestMap(displayName = "a".repeat(350))),
    )

    private fun loginRequestMap(
        login: String? = "testuser",
        password: String? = "password123",
    ): Map<String, Any?> = mapOf(
        "login" to login,
        "password" to password,
    )

    private fun registerRequestMap(
        login: String? = "testuser",
        password: String? = "password123",
        displayName: String? = "Test User",
    ): Map<String, Any?> = mapOf(
        "login" to login,
        "password" to password,
        "displayName" to displayName,
    )
}
