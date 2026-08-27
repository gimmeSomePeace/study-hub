package me.gimmesomepeace.studyhub.semester.exception

import java.util.UUID

class SemesterNotFoundException(
    val semesterId: UUID
) : RuntimeException("Semester with id $semesterId could not be found")
