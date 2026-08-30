package me.gimmesomepeace.studyhub.subject.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import me.gimmesomepeace.studyhub.subject.constraint.SubjectConstraints
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "subject")
data class SubjectEntity(
    @Id
    val id: UUID,

    @Column(name = "semester_id", nullable = false)
    var semesterId: UUID,

    @Column(nullable = false, length = SubjectConstraints.NAME_MAX_LENGTH)
    var name: String,

    @Column(length = SubjectConstraints.CODE_MAX_LENGTH)
    var code: String? = null,

    @Column(length = SubjectConstraints.TEACHER_MAX_LENGTH)
    var teacher: String? = null,

    @Column(length = SubjectConstraints.COLOR_MAX_LENGTH)
    var color: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),
)
