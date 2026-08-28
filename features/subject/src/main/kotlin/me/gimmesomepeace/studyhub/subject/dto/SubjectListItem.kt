package me.gimmesomepeace.studyhub.subject.dto

import java.util.UUID

data class SubjectListItem(
    val id: UUID,
    val semesterId: UUID,
    val name: String,
    val code: String?,
    val teacher: String?,
)
