package me.gimmesomepeace.studyhub.app.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.jwt")
data class JwtProperties(
    var secret: String,
    val expirationMs: Long,
)
