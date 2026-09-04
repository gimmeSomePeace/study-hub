package me.gimmesomepeace.studyhub.deadline.fixtures

import me.gimmesomepeace.studyhub.deadline.dto.DeadlineAction
import me.gimmesomepeace.studyhub.deadline.dto.DeadlineDetails
import me.gimmesomepeace.studyhub.deadline.dto.DeadlineListItem
import me.gimmesomepeace.studyhub.deadline.dto.DeadlineStatus
import me.gimmesomepeace.studyhub.deadline.dto.DeadlineType
import java.time.Instant
import java.util.UUID

fun deadlineAction(
    name: String = "OPEN",
    method: String = "POST",
    href: String = "/api/v1/deadlines/{id}/reopen",
    description: String = "Открыть",
) = DeadlineAction(
    name = name,
    method = method,
    href = href,
    description = description,
)

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
    actions: List<DeadlineAction> = listOf(deadlineAction()),
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
    actions = actions,
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
