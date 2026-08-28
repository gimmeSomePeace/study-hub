package me.gimmesomepeace.studyhub.subject.fixtures

import me.gimmesomepeace.studyhub.subject.dto.SubjectDetails
import me.gimmesomepeace.studyhub.subject.dto.SubjectListItem
import java.time.Instant
import java.util.UUID

fun subjectDetails(
    id: UUID = subjectId(),
    semesterId: UUID = semesterId(),
    name: String = "DEFAULT SUBJECT NAME",
    code: String? = "CS101",
    teacher: String? = "John Doe",
    color: String? = "#3B82F6",
    createdAt: Instant = Instant.parse("2020-01-01T00:00:00Z"),
    updatedAt: Instant = Instant.parse("2020-01-01T00:00:00Z"),
) = SubjectDetails(
    id = id,
    semesterId = semesterId,
    name = name,
    code = code,
    teacher = teacher,
    color = color,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun subjectListItem(
    id: UUID = subjectId(),
    semesterId: UUID = semesterId(),
    name: String = "DEFAULT SUBJECT NAME",
    code: String? = "CS101",
    teacher: String? = "John Doe",
    color: String? = "#3B82F6",
) = SubjectListItem(
    id = id,
    semesterId = semesterId,
    name = name,
    code = code,
    teacher = teacher,
    color = color,
)
