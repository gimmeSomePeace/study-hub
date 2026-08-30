package me.gimmesomepeace.studyhub.subject.service

import me.gimmesomepeace.studyhub.common.IdGenerator
import me.gimmesomepeace.studyhub.subject.api.create.SubjectCreateRequest
import me.gimmesomepeace.studyhub.subject.api.update.SubjectUpdateRequest
import me.gimmesomepeace.studyhub.subject.dto.SubjectDetails
import me.gimmesomepeace.studyhub.subject.dto.SubjectListItem
import me.gimmesomepeace.studyhub.subject.entity.SubjectEntity
import me.gimmesomepeace.studyhub.subject.exception.SubjectNotFoundException
import me.gimmesomepeace.studyhub.subject.exception.SemesterNotFoundException
import me.gimmesomepeace.studyhub.subject.repository.SubjectRepository
import me.gimmesomepeace.studyhub.subject.toDetails
import me.gimmesomepeace.studyhub.subject.toListItem
import org.springframework.dao.DataIntegrityViolationException
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
    private val subjectRepository: SubjectRepository,
) {
    @Transactional(readOnly = true)
    fun getById(id: UUID): SubjectDetails = subjectRepository
        .findById(id)
        .orElseThrow { SubjectNotFoundException(id) }
        .toDetails()

    @Transactional(readOnly = true)
    fun list(pageable: Pageable): Page<SubjectListItem> {
        val page = subjectRepository.findAll(pageable)
        return page.map { it.toListItem() }
    }

    fun create(request: SubjectCreateRequest): SubjectDetails {
        val subject = SubjectEntity(
            id = idGenerator.generate(),
            semesterId = request.semesterId,
            name = request.name,
            code = request.code,
            teacher = request.teacher,
            color = request.color,
        )

        try {
            subjectRepository.saveAndFlush(subject)
        } catch (e: DataIntegrityViolationException) {
            throw mapToIntegrityException(e, subject)
        }

        return subject.toDetails()
    }

    fun update(
        id: UUID,
        request: SubjectUpdateRequest,
    ): SubjectDetails {
        val subject = subjectRepository
            .findById(id)
            .orElseThrow { SubjectNotFoundException(id) }

        if (request.semesterId != null) subject.semesterId = request.semesterId
        if (request.name != null) subject.name = request.name
        if (request.code != null) subject.code = request.code
        if (request.teacher != null) subject.teacher = request.teacher
        if (request.color != null) subject.color = request.color
        subject.updatedAt = Instant.now()

        try {
            subjectRepository.saveAndFlush(subject)
        } catch (e: DataIntegrityViolationException) {
            throw mapToIntegrityException(e, subject)
        }

        return subject.toDetails()
    }

    fun delete(id: UUID) {
        subjectRepository.deleteById(id)
    }

    private fun mapToIntegrityException(
        e: DataIntegrityViolationException,
        subject: SubjectEntity,
    ): RuntimeException {
        val message = e.mostSpecificCause.message ?: ""
        return when {
            message.contains("fk_subject_semester", ignoreCase = true) -> {
                SemesterNotFoundException(subject.semesterId)
            }

            else -> {
                e
            }
        }
    }
}
