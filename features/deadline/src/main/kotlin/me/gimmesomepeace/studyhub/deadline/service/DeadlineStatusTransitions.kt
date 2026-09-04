package me.gimmesomepeace.studyhub.deadline.service

import me.gimmesomepeace.studyhub.deadline.dto.DeadlineStatus
import me.gimmesomepeace.studyhub.deadline.dto.DeadlineStatus.CANCELLED
import me.gimmesomepeace.studyhub.deadline.dto.DeadlineStatus.CLOSED
import me.gimmesomepeace.studyhub.deadline.dto.DeadlineStatus.OPEN
import org.springframework.stereotype.Component

@Component
class DeadlineStatusTransitions {
    private val allowed: Map<DeadlineStatus, Set<DeadlineStatus>> = mapOf(
        OPEN to setOf(CLOSED, CANCELLED),
        CLOSED to setOf(OPEN),
        CANCELLED to setOf(),
    )

    fun canTransitTo(
        from: DeadlineStatus,
        to: DeadlineStatus,
    ): Boolean = allowed[from]?.contains(to) ?: false

    fun availableTransitionsFor(status: DeadlineStatus) = allowed[status] ?: setOf()
}
