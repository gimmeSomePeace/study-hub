package me.gimmesomepeace.studyhub.subject.repository

import me.gimmesomepeace.studyhub.subject.entity.SubjectEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface SubjectRepository : JpaRepository<SubjectEntity, UUID> {
    @Query(
        """
        SELECT s FROM SubjectEntity s
        JOIN  SemesterEntity sem ON s.semesterId = sem.id
        WHERE s.id = :id AND sem.ownerId = :ownerId
    """,
    )
    fun findByIdAndOwnerId(
        @Param("id") id: UUID,
        @Param("ownerId") ownerId: UUID,
    ): SubjectEntity?

    @Query(
        """
        SELECT s FROM SubjectEntity s
        JOIN  SemesterEntity sem ON s.semesterId = sem.id
        WHERE sem.ownerId = :ownerId
    """,
    )
    fun findByOwnerId(
        @Param("ownerId") ownerId: UUID,
        pageable: Pageable,
    ): Page<SubjectEntity>

    @Query(
        """
            DELETE FROM SubjectEntity s
            WHERE s.id = :id AND s.semesterId IN (SELECT id FROM SemesterEntity sem WHERE sem.ownerId = :ownerId)
        """,
    )
    fun deleteByIdAndOwnerId(
        @Param("id") id: UUID,
        @Param("ownerId") ownerId: UUID,
    )

    @Query(
        """
        SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END
        FROM SubjectEntity s
        JOIN SemesterEntity sem ON s.semesterId = sem.id
        WHERE s.id = :id AND sem.ownerId = :ownerId
    """,
    )
    fun existsByIdAndOwnerId(
        @Param("id") id: UUID,
        @Param("ownerId") ownerId: UUID,
    ): Boolean
}
