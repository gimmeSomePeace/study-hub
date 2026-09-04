package me.gimmmesomepeace.studyhub.infrastructure.id

import com.github.f4b6a3.uuid.UuidCreator
import me.gimmesomepeace.studyhub.core.id.IdGenerator
import java.util.UUID

class UuidV7Generator : IdGenerator<UUID> {
    override fun generate(): UUID = UuidCreator.getTimeOrderedEpoch()
}
