package me.gimmesomepeace.studyhub.subject.component.dto

import java.time.Instant
import java.util.UUID

data class ComponentDetails(
    val id: UUID,
    val subjectId: UUID,
    val type: ComponentType,
    val title: String,
    val priority: Int,
    val notes: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
)
