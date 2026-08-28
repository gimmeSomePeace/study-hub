package me.gimmesomepeace.studyhub.subject.fixtures

import me.gimmesomepeace.studyhub.subject.entity.SubjectEntity
import java.time.Instant
import java.util.UUID

fun subjectEntity(
    id: UUID = subjectId(),
    semesterId: UUID = semesterId(),
    name: String = "DEFAULT SUBJECT NAME",
    code: String? = "CS101",
    teacher: String? = "John Doe",
    color: String? = "#3B82F6",
    createdAt: Instant = Instant.parse("2020-01-01T00:00:00Z"),
    updatedAt: Instant = Instant.parse("2020-01-01T00:00:00Z"),
) = SubjectEntity(
    id = id,
    semesterId = semesterId,
    name = name,
    code = code,
    teacher = teacher,
    color = color,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
