package me.gimmesomepeace.studyhub.subject.fixtures

import me.gimmesomepeace.studyhub.common.user.UserPrincipal
import me.gimmesomepeace.studyhub.common.user.UserRole
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import java.util.UUID

fun authenticateAs(
    userId: UUID = userId(),
    role: UserRole = UserRole.USER,
) = authentication(
    UsernamePasswordAuthenticationToken(
        UserPrincipal(userId = userId, role = role),
        null,
        listOf(
            SimpleGrantedAuthority("ROLE_${role.name}"),
        ),
    ),
)
