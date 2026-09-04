package me.gimmesomepeace.studyhub.app.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import me.gimmesomepeace.studyhub.core.token.TokenProvider
import me.gimmesomepeace.studyhub.core.user.UserPrincipal
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import kotlin.text.removePrefix
import kotlin.text.startsWith

@Component
class AuthenticationFilter(
    private val tokenProvider: TokenProvider,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val token = extractToken(request)

        if (token != null) {
            try {
                val parsed = tokenProvider.parseToken(token)
                val principal = UserPrincipal(userId = parsed.userId, role = parsed.role)

                val authentication = UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    emptyList(),
                )

                SecurityContextHolder.getContext().authentication = authentication
            } catch (_: Exception) {
                SecurityContextHolder.clearContext()
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun extractToken(request: HttpServletRequest): String? {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (header == null || !header.startsWith("Bearer ")) return null

        return header.removePrefix("Bearer ")
    }
}
