package me.gimmesomepeace.studyhub.deadline.service

import me.gimmesomepeace.studyhub.common.id.IdGenerator
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
    fun getById(
        id: UUID,
        userId: UUID,
    ): DeadlineDetails = deadlineRepository
        .findByIdAndOwnerId(id, userId)
        ?.toDetails()
        ?: throw DeadlineNotFoundException(id)

    @Transactional(readOnly = true)
    fun list(
        pageable: Pageable,
        userId: UUID,
    ): Page<DeadlineListItem> {
        val page = deadlineRepository.findByOwnerId(userId, pageable)
        return page.map { it.toListItem() }
    }

    fun create(
        request: DeadlineCreateRequest,
        userId: UUID,
    ): DeadlineDetails {
        ensureSubjectExistsAndBelongsToOwner(userId, request.subjectId)
        if (request.componentId != null) {
            ensureComponentExistsAndBelongsToSubject(request.componentId, request.subjectId)
        }

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
        userId: UUID,
    ): DeadlineDetails {
        val deadline = deadlineRepository
            .findById(id)
            .orElseThrow { DeadlineNotFoundException(id) }

        if (request.subjectId != null) {
            ensureSubjectExistsAndBelongsToOwner(userId, request.subjectId)
            deadline.subjectId = request.subjectId
        }
        if (request.componentId != null) {
            ensureComponentExistsAndBelongsToSubject(userId, request.componentId)
            deadline.componentId = request.componentId
        }

        if (request.title != null) deadline.title = request.title
        if (request.dueAt != null) deadline.dueAt = request.dueAt
        if (request.type != null) deadline.type = request.type
        if (request.notes != null) deadline.notes = request.notes

        deadline.updatedAt = Instant.now()

        deadlineRepository.save(deadline)
        return deadline.toDetails()
    }

    fun closeDeadline(
        id: UUID,
        userId: UUID,
    ): DeadlineDetails = updateDeadlineStatus(id, DeadlineStatus.CLOSED, userId)

    fun reopenDeadline(
        id: UUID,
        userId: UUID,
    ): DeadlineDetails = updateDeadlineStatus(id, DeadlineStatus.OPEN, userId)

    fun cancelDeadline(
        id: UUID,
        userId: UUID,
    ): DeadlineDetails = updateDeadlineStatus(id, DeadlineStatus.CANCELLED, userId)

    private fun updateDeadlineStatus(
        id: UUID,
        newStatus: DeadlineStatus,
        userId: UUID,
    ): DeadlineDetails {
        val deadline = deadlineRepository
            .findByIdAndOwnerId(id, userId)
            ?: throw DeadlineNotFoundException(id)

        if (deadline.status == newStatus) return deadline.toDetails()

        if (!statusTransitions.canTransitTo(deadline.status, newStatus)) {
            throw InvalidStatusTransitionException(deadline.status, newStatus)
        }
        deadline.status = newStatus
        deadline.updatedAt = Instant.now()
        return deadlineRepository.save(deadline).toDetails()
    }

    fun delete(
        id: UUID,
        userId: UUID,
    ) {
        deadlineRepository.deleteByIdAndOwnerId(id, userId)
    }

    private fun ensureSubjectExistsAndBelongsToOwner(
        userId: UUID,
        subjectId: UUID,
    ) {
        if (!subjectRepository.existsByIdAndOwnerId(subjectId, userId)) {
            throw SubjectNotFoundException(subjectId)
        }
    }

    private fun ensureComponentExistsAndBelongsToSubject(
        componentId: UUID,
        subjectId: UUID,
    ) {
        if (!componentRepository.existsByIdAndSubjectId(componentId, subjectId)) {
            throw ComponentNotFoundException(componentId)
        }
    }
}
