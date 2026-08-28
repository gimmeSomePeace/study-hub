package me.gimmesomepeace.studyhub.subject.exception

import java.util.UUID

class NotFoundException(
    val id: UUID,
) : RuntimeException("Subject with id '$id' not found")
