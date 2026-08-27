package me.gimmesomepeace.studyhub.semester.entity

import me.gimmesomepeace.studyhub.semester.dto.semesterId
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

fun semesterEntity(
    id: UUID = semesterId(),
    name: String = "DEFAULT SEMESTER NAME",
    startsAt: LocalDate = LocalDate.of(2020, 1, 1),
    endsAt: LocalDate = LocalDate.of(2021, 1, 1),
    createdAt: Instant = Instant.parse("2020-01-01T00:00:00Z"),
    updatedAt: Instant = Instant.parse("2020-01-01T00:00:00Z"),
) = SemesterEntity(
    id = id,
    name = name,
    startsAt = startsAt,
    endsAt = endsAt,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
