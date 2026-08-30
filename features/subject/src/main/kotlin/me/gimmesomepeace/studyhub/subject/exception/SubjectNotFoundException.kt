package me.gimmesomepeace.studyhub.subject.exception

import java.util.UUID

class SubjectNotFoundException(
    val id: UUID,
) : RuntimeException("Subject with id '$id' not found")
