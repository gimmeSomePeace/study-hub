package me.gimmesomepeace.studyhub.deadline.exception

import me.gimmesomepeace.studyhub.deadline.dto.DeadlineStatus

class InvalidStatusTransitionException(
    val from: DeadlineStatus,
    val to: DeadlineStatus,
) : RuntimeException("Cannot transition from '$from' to '$to'")
