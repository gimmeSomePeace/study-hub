package me.gimmesomepeace.studyhub.subject.component.service

import me.gimmesomepeace.studyhub.common.IdGenerator
import me.gimmesomepeace.studyhub.subject.component.api.create.ComponentCreateRequest
import me.gimmesomepeace.studyhub.subject.component.api.update.ComponentUpdateRequest
import me.gimmesomepeace.studyhub.subject.component.dto.ComponentDetails
import me.gimmesomepeace.studyhub.subject.component.dto.ComponentListItem
import me.gimmesomepeace.studyhub.subject.component.entity.ComponentEntity
import me.gimmesomepeace.studyhub.subject.component.exception.ComponentNotFoundException
import me.gimmesomepeace.studyhub.subject.component.repository.ComponentRepository
import me.gimmesomepeace.studyhub.subject.exception.SubjectNotFoundException
import me.gimmesomepeace.studyhub.subject.repository.SubjectRepository
import me.gimmesomepeace.studyhub.subject.component.toDetails
import me.gimmesomepeace.studyhub.subject.component.toListItem
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
@Transactional
class ComponentService(
    private val idGenerator: IdGenerator<UUID>,
    private val componentRepository: ComponentRepository,
    private val subjectRepository: SubjectRepository,
) {
    @Transactional(readOnly = true)
    fun getById(
        subjectId: UUID,
        id: UUID,
    ): ComponentDetails {
        ensureSubjectExists(subjectId)

        return componentRepository
            .findById(id)
            .filter { it.subjectId == subjectId }
            .orElseThrow { ComponentNotFoundException(id) }
            .toDetails()
    }

    @Transactional(readOnly = true)
    fun list(
        subjectId: UUID,
        pageable: Pageable,
    ): Page<ComponentListItem> {
        ensureSubjectExists(subjectId)

        val page = componentRepository.findBySubjectId(subjectId, pageable)
        return page.map { it.toListItem() }
    }

    fun create(
        subjectId: UUID,
        request: ComponentCreateRequest,
    ): ComponentDetails {
        ensureSubjectExists(subjectId)

        val component = ComponentEntity(
            id = idGenerator.generate(),
            subjectId = subjectId,
            type = request.type,
            title = request.title,
            priority = request.priority,
            notes = request.notes,
        )

        componentRepository.save(component)
        return component.toDetails()
    }

    fun update(
        subjectId: UUID,
        id: UUID,
        request: ComponentUpdateRequest,
    ): ComponentDetails {
        ensureSubjectExists(subjectId)

        val component = componentRepository
            .findById(id)
            .filter { it.subjectId == subjectId }
            .orElseThrow { ComponentNotFoundException(id) }

        if (request.type != null) component.type = request.type
        if (request.title != null) component.title = request.title
        if (request.priority != null) component.priority = request.priority
        if (request.notes != null) component.notes = request.notes
        component.updatedAt = Instant.now()

        componentRepository.save(component)
        return component.toDetails()
    }

    fun delete(
        subjectId: UUID,
        id: UUID,
    ) {
        ensureSubjectExists(subjectId)
        componentRepository.deleteBySubjectIdAndId(subjectId, id)
    }

    private fun ensureSubjectExists(subjectId: UUID) {
        if (!subjectRepository.existsById(subjectId)) {
            throw SubjectNotFoundException(subjectId)
        }
    }
}
