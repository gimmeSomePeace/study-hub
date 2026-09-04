package me.gimmesomepeace.studyhub.subject.service

import me.gimmesomepeace.studyhub.core.id.IdGenerator
import me.gimmesomepeace.studyhub.semester.repository.SemesterRepository
import me.gimmesomepeace.studyhub.subject.api.create.SubjectCreateRequest
import me.gimmesomepeace.studyhub.subject.api.update.SubjectUpdateRequest
import me.gimmesomepeace.studyhub.subject.dto.SubjectDetails
import me.gimmesomepeace.studyhub.subject.dto.SubjectListItem
import me.gimmesomepeace.studyhub.subject.entity.SubjectEntity
import me.gimmesomepeace.studyhub.subject.exception.SemesterNotFoundException
import me.gimmesomepeace.studyhub.subject.exception.SubjectNotFoundException
import me.gimmesomepeace.studyhub.subject.repository.SubjectRepository
import me.gimmesomepeace.studyhub.subject.toDetails
import me.gimmesomepeace.studyhub.subject.toListItem
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
@Transactional
class SubjectService(
    private val idGenerator: IdGenerator<UUID>,
    private val semesterRepository: SemesterRepository,
    private val subjectRepository: SubjectRepository,
) {
    @Transactional(readOnly = true)
    fun getById(
        id: UUID,
        userId: UUID,
    ): SubjectDetails = subjectRepository
        .findByIdAndOwnerId(id, userId)
        ?.toDetails()
        ?: throw SubjectNotFoundException(id)

    @Transactional(readOnly = true)
    fun list(
        pageable: Pageable,
        userId: UUID,
    ): Page<SubjectListItem> {
        val page = subjectRepository.findByOwnerId(userId, pageable)
        return page.map { it.toListItem() }
    }

    fun create(
        request: SubjectCreateRequest,
        userId: UUID,
    ): SubjectDetails {
        ensureSemesterExistsAndBelongsToUser(request.semesterId, userId)

        val subject = SubjectEntity(
            id = idGenerator.generate(),
            semesterId = request.semesterId,
            name = request.name,
            code = request.code,
            teacher = request.teacher,
            color = request.color,
        )

        subjectRepository.save(subject)
        return subject.toDetails()
    }

    fun update(
        id: UUID,
        request: SubjectUpdateRequest,
        userId: UUID,
    ): SubjectDetails {
        val subject = subjectRepository
            .findByIdAndOwnerId(id, userId)
            ?: throw SubjectNotFoundException(id)

        if (request.semesterId != null) {
            ensureSemesterExistsAndBelongsToUser(request.semesterId, userId)
            subject.semesterId = request.semesterId
        }
        if (request.name != null) subject.name = request.name
        if (request.code != null) subject.code = request.code
        if (request.teacher != null) subject.teacher = request.teacher
        if (request.color != null) subject.color = request.color
        subject.updatedAt = Instant.now()

        subjectRepository.save(subject)
        return subject.toDetails()
    }

    fun delete(
        id: UUID,
        userId: UUID,
    ) {
        subjectRepository.deleteByIdAndOwnerId(id, userId)
    }

    private fun ensureSemesterExistsAndBelongsToUser(
        semesterId: UUID,
        userId: UUID,
    ) {
        if (!semesterRepository.existsByIdAndOwnerId(semesterId, userId)) {
            throw SemesterNotFoundException(semesterId)
        }
    }
}
