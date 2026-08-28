package me.gimmesomepeace.studyhub.subject

import me.gimmesomepeace.studyhub.subject.dto.SubjectDetails
import me.gimmesomepeace.studyhub.subject.dto.SubjectListItem
import me.gimmesomepeace.studyhub.subject.entity.SubjectEntity

fun SubjectEntity.toDetails() = SubjectDetails(
    id = id,
    semesterId = semesterId,
    name = name,
    code = code,
    teacher = teacher,
    color = color,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun SubjectEntity.toListItem() = SubjectListItem(
    id = id,
    semesterId = semesterId,
    name = name,
    code = code,
    teacher = teacher,
    color = color,
)
