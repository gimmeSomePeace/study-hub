package me.gimmesomepeace.studyhub.deadline.repository

import me.gimmesomepeace.studyhub.deadline.entity.DeadlineEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface DeadlineRepository : JpaRepository<DeadlineEntity, UUID> {
    @Query(
        """
        SELECT d FROM DeadlineEntity d
        JOIN SubjectEntity s ON d.subjectId = s.id
        JOIN SemesterEntity sem ON s.semesterId = sem.id
        WHERE d.id = :id AND sem.ownerId = :ownerId
    """,
    )
    fun findByIdAndOwnerId(
        @Param("id") id: UUID,
        @Param("ownerId") ownerId: UUID,
    ): DeadlineEntity?

    @Query(
        """
        SELECT d FROM DeadlineEntity d
        JOIN SubjectEntity s ON d.subjectId = s.id
        JOIN SemesterEntity sem ON s.semesterId = sem.id
        WHERE sem.ownerId = :ownerId
    """,
    )
    fun findByOwnerId(
        @Param("ownerId") ownerId: UUID,
        pageable: Pageable,
    ): Page<DeadlineEntity>

    @Modifying
    @Query(
        """
        DELETE FROM DeadlineEntity d
        WHERE id = :id
        AND d.subjectId IN (
            SELECT DISTINCT s.id FROM SubjectEntity s
            WHERE s.id IN (
                SELECT DISTINCT sem.id FROM SemesterEntity sem WHERE sem.ownerId = :ownerId
            )
        )
    """,
    )
    fun deleteByIdAndOwnerId(
        @Param("id") id: UUID,
        @Param("ownerId") ownerId: UUID,
    )
}
