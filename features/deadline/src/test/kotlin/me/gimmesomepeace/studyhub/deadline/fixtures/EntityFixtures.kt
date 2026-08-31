package me.gimmesomepeace.studyhub.deadline.fixtures

import me.gimmesomepeace.studyhub.deadline.dto.DeadlineStatus
import me.gimmesomepeace.studyhub.deadline.dto.DeadlineType
import me.gimmesomepeace.studyhub.deadline.entity.DeadlineEntity
import me.gimmesomepeace.studyhub.subject.component.dto.ComponentType
import me.gimmesomepeace.studyhub.subject.component.entity.ComponentEntity
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

fun futureDueAt(): Instant = Instant.now().plus(7, ChronoUnit.DAYS)

fun pastDueAt(): Instant = Instant.now().minus(7, ChronoUnit.DAYS)

fun deadlineEntity(
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
) = DeadlineEntity(
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

fun deadlineComponent(
    id: UUID = deadlineComponentId(),
    subjectId: UUID = deadlineSubjectId(),
    title: String = "Лекция 1",
) = ComponentEntity(
    id = id,
    subjectId = subjectId,
    type = ComponentType.LECTURE,
    title = title,
    priority = 3,
)
