package me.gimmesomepeace.studyhub.subject.exception

import java.util.UUID

class SemesterNotFoundException(
    val semesterId: UUID,
) : RuntimeException("Semester with id '$semesterId' not found")
