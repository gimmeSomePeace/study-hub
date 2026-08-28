package me.gimmesomepeace.studyhub.subject.dto

import java.time.Instant
import java.util.UUID

data class SubjectDetails(
    val id: UUID,
    val semesterId: UUID,
    val name: String,
    val code: String?,
    val teacher: String?,
    val color: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
)
