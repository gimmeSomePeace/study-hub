package me.gimmesomepeace.studyhub.semester.dto

import java.time.Instant
import java.time.LocalDate
import java.util.UUID

fun semesterId(): UUID = UUID.randomUUID()

fun semesterDetails(
    id: UUID = semesterId(),
    name: String = "DEFAULT SEMESTER NAME",
    startsAt: LocalDate = LocalDate.of(2020, 1, 1),
    endsAt: LocalDate = LocalDate.of(2021, 1, 1),
    createdAt: Instant = Instant.now(),
    updatedAt: Instant = Instant.now(),
) = SemesterDetails(
    id = id,
    name = name,
    startsAt = startsAt,
    endsAt = endsAt,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun semesterListItem(
    id: UUID = semesterId(),
    name: String = "DEFAULT SEMESTER NAME",
    startsAt: LocalDate = LocalDate.of(2020, 1, 1),
    endsAt: LocalDate = LocalDate.of(2021, 1, 1),
) = SemesterListItem(
    id = id,
    name = name,
    startsAt = startsAt,
    endsAt = endsAt,
)
