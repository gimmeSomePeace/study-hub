package me.gimmesomepeace.studyhub.deadline.fixtures

import me.gimmesomepeace.studyhub.deadline.dto.DeadlineDetails
import me.gimmesomepeace.studyhub.deadline.dto.DeadlineListItem
import me.gimmesomepeace.studyhub.deadline.dto.DeadlineStatus
import me.gimmesomepeace.studyhub.deadline.dto.DeadlineType
import java.time.Instant
import java.util.UUID

fun deadlineDetails(
    id: UUID = deadlineId(),
    subjectId: UUID = deadlineSubjectId(),
    componentId: UUID? = null,
    title: String = "DEFAULT DEADLINE TITLE",
    dueAt: Instant = futureDueAt(),
    type: DeadlineType = DeadlineType.LAB,
    status: DeadlineStatus = DeadlineStatus.OPEN,
    notes: String? = "Default notes",
    createdAt: Instant = Instant.parse("2026-01-01T00:00:00Z"),
    updatedAt: Instant = Instant.parse("2026-01-01T00:00:00Z"),
) = DeadlineDetails(
    id = id,
    subjectId = subjectId,
    componentId = componentId,
    title = title,
    dueAt = dueAt,
    type = type,
    status = status,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun deadlineListItem(
    id: UUID = deadlineId(),
    subjectId: UUID = deadlineSubjectId(),
    componentId: UUID? = null,
    title: String = "DEFAULT DEADLINE TITLE",
    status: DeadlineStatus = DeadlineStatus.OPEN,
    type: DeadlineType = DeadlineType.LAB,
    dueAt: Instant = futureDueAt(),
) = DeadlineListItem(
    id = id,
    subjectId = subjectId,
    componentId = componentId,
    title = title,
    status = status,
    type = type,
    dueAt = dueAt,
)
