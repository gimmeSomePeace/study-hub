package me.gimmesomepeace.studyhub.semester.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import me.gimmesomepeace.studyhub.semester.SemesterConstraints
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "semesters")
class SemesterEntity(
    @Id
    var id: UUID,

    @Column(name = "owner_id", nullable = false)
    var ownerId: UUID,

    @Column(nullable = false, length = SemesterConstraints.NAME_MAX_LENGTH)
    var name: String,

    @Column(name = "starts_at", nullable = false)
    var startsAt: LocalDate,

    @Column(name = "ends_at", nullable = false)
    var endsAt: LocalDate,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),
)
