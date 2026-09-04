package me.gimmesomepeace.studyhub.deadline.dto

import java.time.Instant
import java.util.UUID

data class DeadlineDetails(
    val id: UUID,
    val subjectId: UUID,
    val componentId: UUID?,
    val status: DeadlineStatus,
    val type: DeadlineType,
    val title: String,
    val dueAt: Instant,
    val notes: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val actions: List<DeadlineAction>,
)
