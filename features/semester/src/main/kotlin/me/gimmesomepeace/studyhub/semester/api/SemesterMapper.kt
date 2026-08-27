package me.gimmesomepeace.studyhub.semester.api

import me.gimmesomepeace.studyhub.semester.dto.SemesterDetails
import me.gimmesomepeace.studyhub.semester.dto.SemesterListItem
import me.gimmesomepeace.studyhub.semester.entity.SemesterEntity

fun SemesterEntity.toDetails() = SemesterDetails(
    id = this.id,
    name = this.name,
    startsAt = this.startsAt,
    endsAt = this.endsAt,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)

fun SemesterEntity.toListItem() = SemesterListItem(
    id = this.id,
    name = this.name,
    startsAt = this.startsAt,
    endsAt = this.endsAt,
)
