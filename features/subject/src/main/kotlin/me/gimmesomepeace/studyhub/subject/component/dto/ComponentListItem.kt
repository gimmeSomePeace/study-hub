package me.gimmesomepeace.studyhub.subject.component.dto

import java.util.UUID

data class ComponentListItem(
    val id: UUID,
    val subjectId: UUID,
    val type: ComponentType,
    val title: String,
    val priority: Int,
)

