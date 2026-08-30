package me.gimmesomepeace.studyhub.subject.component.fixtures

import me.gimmesomepeace.studyhub.subject.component.dto.ComponentType
import me.gimmesomepeace.studyhub.subject.component.entity.ComponentEntity
import me.gimmesomepeace.studyhub.subject.fixtures.subjectId
import java.time.Instant
import java.util.UUID

fun componentEntity(
    id: UUID = componentId(),
    subjectId: UUID = subjectId(),
    type: ComponentType = ComponentType.LECTURE,
    title: String = "DEFAULT COMPONENT TITLE",
    priority: Int = 3,
    notes: String? = "Default notes",
    createdAt: Instant = Instant.parse("2020-01-01T00:00:00Z"),
    updatedAt: Instant = Instant.parse("2020-01-01T00:00:00Z"),
) = ComponentEntity(
    id = id,
    subjectId = subjectId,
    type = type,
    title = title,
    priority = priority,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
