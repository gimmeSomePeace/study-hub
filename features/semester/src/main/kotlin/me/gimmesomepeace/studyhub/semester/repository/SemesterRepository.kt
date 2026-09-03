package me.gimmesomepeace.studyhub.semester.repository

import me.gimmesomepeace.studyhub.semester.entity.SemesterEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SemesterRepository : JpaRepository<SemesterEntity, UUID> {
    fun findByIdAndOwnerId(
        id: UUID,
        ownerId: UUID,
    ): SemesterEntity?

    fun findByOwnerId(
        ownerId: UUID,
        pageable: Pageable,
    ): Page<SemesterEntity>

    fun deleteByIdAndOwnerId(
        id: UUID,
        ownerId: UUID,
    )

    fun existsByIdAndOwnerId(
        id: UUID,
        ownerId: UUID,
    ): Boolean
}
