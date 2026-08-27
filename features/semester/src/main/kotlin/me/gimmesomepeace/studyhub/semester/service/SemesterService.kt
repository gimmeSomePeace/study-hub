package me.gimmesomepeace.studyhub.semester.service

import me.gimmesomepeace.studyhub.common.IdGenerator
import me.gimmesomepeace.studyhub.semester.api.create.SemesterCreateRequest
import me.gimmesomepeace.studyhub.semester.api.toDetails
import me.gimmesomepeace.studyhub.semester.api.toListItem
import me.gimmesomepeace.studyhub.semester.api.update.SemesterUpdateRequest
import me.gimmesomepeace.studyhub.semester.dto.SemesterDetails
import me.gimmesomepeace.studyhub.semester.dto.SemesterListItem
import me.gimmesomepeace.studyhub.semester.entity.SemesterEntity
import me.gimmesomepeace.studyhub.semester.exception.SemesterNotFoundException
import me.gimmesomepeace.studyhub.semester.exception.SemesterValidationException
import me.gimmesomepeace.studyhub.semester.repository.SemesterRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class SemesterService(
    private val idGenerator: IdGenerator<UUID>,
    private val semesterRepository: SemesterRepository,
) {
    @Transactional(readOnly = true)
    fun getById(id: UUID): SemesterDetails =
        semesterRepository.findById(id).orElseThrow { SemesterNotFoundException(id) }.toDetails()

    @Transactional(readOnly = true)
    fun list(pageable: Pageable): Page<SemesterListItem> = semesterRepository.findAll(pageable).map { it.toListItem() }

    @Transactional
    fun create(request: SemesterCreateRequest): SemesterDetails {
        if (!request.endsAt.isAfter(request.startsAt)) {
            throw SemesterValidationException("End date must be after start date")
        }
        val semester = SemesterEntity(
            id = idGenerator.generate(),
            name = request.name,
            startsAt = request.startsAt,
            endsAt = request.endsAt,
        )

        semesterRepository.save(semester)
        return semester.toDetails()
    }

    @Transactional
    fun update(
        id: UUID,
        request: SemesterUpdateRequest,
    ): SemesterDetails {
        val semester = semesterRepository.findById(id).orElseThrow { SemesterNotFoundException(id) }

        if (request.startsAt != null) semester.startsAt = request.startsAt
        if (request.endsAt != null) semester.endsAt = request.endsAt
        if (request.name != null) semester.name = request.name

        if (!semester.endsAt.isAfter(semester.startsAt)) {
            throw SemesterValidationException("End date must be after start date")
        }

        semesterRepository.save(semester)
        return semester.toDetails()
    }

    @Transactional
    fun delete(id: UUID) {
        semesterRepository.deleteById(id)
    }
}
