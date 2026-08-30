package me.gimmesomepeace.studyhub.subject.component.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import me.gimmesomepeace.studyhub.subject.component.constraint.ComponentConstraints
import me.gimmesomepeace.studyhub.subject.component.dto.ComponentType
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "subject_components")
data class ComponentEntity(
    @Id
    val id: UUID,

    @Column(nullable = false)
    val subjectId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var type: ComponentType,

    @Column(nullable = false, length = ComponentConstraints.TITLE_MAX_LENGTH)
    var title: String,

    @Column(nullable = false)
    var priority: Int = ComponentConstraints.PRIORITY_DEFAULT,

    @Column(length = ComponentConstraints.NOTES_MAX_LENGTH)
    var notes: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),
)
