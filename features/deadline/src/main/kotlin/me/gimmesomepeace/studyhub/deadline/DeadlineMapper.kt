package me.gimmesomepeace.studyhub.deadline

import me.gimmesomepeace.studyhub.deadline.dto.DeadlineDetails
import me.gimmesomepeace.studyhub.deadline.dto.DeadlineListItem
import me.gimmesomepeace.studyhub.deadline.entity.DeadlineEntity

fun DeadlineEntity.toDetails() = DeadlineDetails(
    id = id,
    subjectId = subjectId,
    componentId = componentId,
    status = status,
    type = type,
    title = title,
    dueAt = dueAt,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun DeadlineEntity.toListItem() = DeadlineListItem(
    id = id,
    subjectId = subjectId,
    componentId = componentId,
    title = title,
    status = status,
    type = type,
    dueAt = dueAt,
)
