package me.gimmesomepeace.studyhub.deadline.service

import me.gimmesomepeace.studyhub.deadline.dto.DeadlineStatus
import org.springframework.stereotype.Component

@Component
class DeadlineStatusTransitions {
    fun canTransitTo(
        from: DeadlineStatus,
        to: DeadlineStatus,
    ): Boolean = when (from) {
        DeadlineStatus.OPEN -> to in setOf(DeadlineStatus.CLOSED, DeadlineStatus.CANCELLED)
        DeadlineStatus.CLOSED -> to in setOf(DeadlineStatus.OPEN)
        DeadlineStatus.CANCELLED -> false
    }
}
