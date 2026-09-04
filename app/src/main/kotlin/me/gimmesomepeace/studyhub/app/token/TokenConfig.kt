package me.gimmesomepeace.studyhub.app.token

import me.gimmesomepeace.studyhub.app.property.JwtProperties
import me.gimmesomepeace.studyhub.core.token.TokenProvider
import me.gimmmesomepeace.studyhub.infrastructure.token.JwtTokenProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TokenConfig {
    @Bean
    fun jwtTokenProvider(properties: JwtProperties): TokenProvider = JwtTokenProvider(
        properties.secret,
        properties.expirationMs,
    )
}
