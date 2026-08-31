package me.gimmesomepeace.studyhub.deadline.dto

import java.time.Instant
import java.util.UUID

data class DeadlineListItem(
    val id: UUID,
    val subjectId: UUID,
    val componentId: UUID?,
    val status: DeadlineStatus,
    val type: DeadlineType,
    val title: String,
    val dueAt: Instant,
)
