package me.gimmesomepeace.studyhub.app.id

import me.gimmesomepeace.studyhub.core.id.IdGenerator
import me.gimmmesomepeace.studyhub.infrastructure.id.UuidV7Generator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.UUID

@Configuration
class IdConfig {
    @Bean
    fun idGenerator(): IdGenerator<UUID> = UuidV7Generator()
}
