package me.gimmesomepeace.studyhub.semester.dto

import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class SemesterDetails(
    val id: UUID,
    val name: String,
    val startsAt: LocalDate,
    val endsAt: LocalDate,
    val createdAt: Instant,
    val updatedAt: Instant,
)
