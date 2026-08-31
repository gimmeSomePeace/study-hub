package me.gimmesomepeace.studyhub.deadline.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import me.gimmesomepeace.studyhub.deadline.DeadlineConstraints
import me.gimmesomepeace.studyhub.deadline.dto.DeadlineStatus
import me.gimmesomepeace.studyhub.deadline.dto.DeadlineType
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "deadlines")
data class DeadlineEntity(
    @Id
    val id: UUID,

    @Column(name = "subject_id", nullable = false)
    var subjectId: UUID,

    @Column(name = "component_id")
    var componentId: UUID? = null,

    @Column(nullable = false, length = DeadlineConstraints.TITLE_MAX_LENGTH)
    var title: String,

    @Column(name = "due_at", nullable = false)
    var dueAt: Instant,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: DeadlineType,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: DeadlineStatus = DeadlineConstraints.DEFAULT_STATUS,

    @Column(length = DeadlineConstraints.NOTES_MAX_LENGTH)
    var notes: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),
)
