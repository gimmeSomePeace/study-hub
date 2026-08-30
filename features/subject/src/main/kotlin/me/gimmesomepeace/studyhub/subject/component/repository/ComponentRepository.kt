package me.gimmesomepeace.studyhub.subject.component.repository

import me.gimmesomepeace.studyhub.subject.component.entity.ComponentEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ComponentRepository : JpaRepository<ComponentEntity, UUID> {
    fun findBySubjectId(
        subjectId: UUID,
        pageable: Pageable,
    ): Page<ComponentEntity>

    fun deleteBySubjectIdAndId(
        subjectId: UUID,
        id: UUID,
    )
}
