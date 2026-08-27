package me.gimmesomepeace.studyhub.semester.dto

import java.time.LocalDate
import java.util.UUID

data class SemesterListItem(
    val id: UUID,
    val name: String,
    val startsAt: LocalDate,
    val endsAt: LocalDate,
)
