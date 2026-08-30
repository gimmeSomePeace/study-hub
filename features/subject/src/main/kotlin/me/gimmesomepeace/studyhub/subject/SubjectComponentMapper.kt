package me.gimmesomepeace.studyhub.subject

import me.gimmesomepeace.studyhub.subject.component.dto.ComponentDetails
import me.gimmesomepeace.studyhub.subject.component.dto.ComponentListItem
import me.gimmesomepeace.studyhub.subject.component.entity.ComponentEntity

fun ComponentEntity.toDetails() = ComponentDetails(
    id = id,
    subjectId = subjectId,
    type = type,
    title = title,
    priority = priority,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun ComponentEntity.toListItem() = ComponentListItem(
    id = id,
    subjectId = subjectId,
    type = type,
    title = title,
    priority = priority,
)
