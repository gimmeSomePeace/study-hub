package me.gimmesomepeace.studyhub.deadline.service

import jakarta.validation.ValidationException
import me.gimmesomepeace.studyhub.common.IdGenerator
import me.gimmesomepeace.studyhub.deadline.api.create.DeadlineCreateRequest
import me.gimmesomepeace.studyhub.deadline.api.update.DeadlineUpdateRequest
import me.gimmesomepeace.studyhub.deadline.dto.DeadlineDetails
import me.gimmesomepeace.studyhub.deadline.dto.DeadlineListItem
import me.gimmesomepeace.studyhub.deadline.dto.DeadlineStatus
import me.gimmesomepeace.studyhub.deadline.entity.DeadlineEntity
import me.gimmesomepeace.studyhub.deadline.exception.DeadlineNotFoundException
import me.gimmesomepeace.studyhub.deadline.exception.InvalidStatusTransitionException
import me.gimmesomepeace.studyhub.deadline.repository.DeadlineRepository
import me.gimmesomepeace.studyhub.deadline.toDetails
import me.gimmesomepeace.studyhub.deadline.toListItem
import me.gimmesomepeace.studyhub.subject.component.exception.ComponentNotFoundException
import me.gimmesomepeace.studyhub.subject.component.repository.ComponentRepository
import me.gimmesomepeace.studyhub.subject.exception.SubjectNotFoundException
import me.gimmesomepeace.studyhub.subject.repository.SubjectRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
@Transactional
class DeadlineService(
    private val idGenerator: IdGenerator<UUID>,
    private val deadlineRepository: DeadlineRepository,
    private val subjectRepository: SubjectRepository,
    private val componentRepository: ComponentRepository,
    private val statusTransitions: DeadlineStatusTransitions,
) {
    @Transactional(readOnly = true)
    fun getById(id: UUID): DeadlineDetails = deadlineRepository
        .findById(id)
        .orElseThrow { DeadlineNotFoundException(id) }
        .toDetails()

    @Transactional(readOnly = true)
    fun list(pageable: Pageable): Page<DeadlineListItem> {
        val page = deadlineRepository.findAll(pageable)
        return page.map { it.toListItem() }
    }

    fun create(request: DeadlineCreateRequest): DeadlineDetails {
        ensureReferences(request.subjectId, request.componentId)

        val deadline = DeadlineEntity(
            id = idGenerator.generate(),
            subjectId = request.subjectId,
            componentId = request.componentId,
            type = request.type,
            title = request.title,
            dueAt = request.dueAt,
            notes = request.notes,
        )

        deadlineRepository.save(deadline)
        return deadline.toDetails()
    }

    fun update(
        id: UUID,
        request: DeadlineUpdateRequest,
    ): DeadlineDetails {
        val deadline = deadlineRepository
            .findById(id)
            .orElseThrow { DeadlineNotFoundException(id) }

        if (request.subjectId != null) deadline.subjectId = request.subjectId
        if (request.componentId != null) deadline.componentId = request.componentId
        ensureReferences(deadline.subjectId, request.componentId)

        if (request.title != null) deadline.title = request.title
        if (request.dueAt != null) deadline.dueAt = request.dueAt
        if (request.type != null) deadline.type = request.type
        if (request.notes != null) deadline.notes = request.notes

        deadline.updatedAt = Instant.now()

        deadlineRepository.save(deadline)
        return deadline.toDetails()
    }

    fun closeDeadline(id: UUID): DeadlineDetails = updateDeadlineStatus(id, DeadlineStatus.CLOSED)

    fun reopenDeadline(id: UUID): DeadlineDetails = updateDeadlineStatus(id, DeadlineStatus.OPEN)

    fun cancelDeadline(id: UUID): DeadlineDetails = updateDeadlineStatus(id, DeadlineStatus.CANCELLED)

    private fun updateDeadlineStatus(
        id: UUID,
        newStatus: DeadlineStatus,
    ): DeadlineDetails {
        val deadline = deadlineRepository
            .findById(id)
            .orElseThrow { DeadlineNotFoundException(id) }

        if (deadline.status == newStatus) return deadline.toDetails()

        if (!statusTransitions.canTransitTo(deadline.status, newStatus)) {
            throw InvalidStatusTransitionException(deadline.status, newStatus)
        }
        deadline.status = newStatus
        deadline.updatedAt = Instant.now()
        return deadlineRepository.save(deadline).toDetails()
    }

    fun delete(id: UUID) {
        deadlineRepository.deleteById(id)
    }

    private fun ensureReferences(
        subjectId: UUID,
        componentId: UUID?,
    ) {
        if (!subjectRepository.existsById(subjectId)) {
            throw SubjectNotFoundException(subjectId)
        }

        if (componentId != null) {
            val component = componentRepository
                .findById(componentId)
                .orElseThrow { ComponentNotFoundException(componentId) }

            if (component.subjectId != subjectId) {
                throw ValidationException(
                    "Component with id '$componentId' does not belong to subject with id '$subjectId'",
                )
            }
        }
    }
}
