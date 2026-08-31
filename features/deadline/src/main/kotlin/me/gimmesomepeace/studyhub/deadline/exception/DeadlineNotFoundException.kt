package me.gimmesomepeace.studyhub.deadline.exception

import java.util.UUID

class DeadlineNotFoundException(
    val id: UUID,
) : RuntimeException("Deadline with id '$id' not found")
