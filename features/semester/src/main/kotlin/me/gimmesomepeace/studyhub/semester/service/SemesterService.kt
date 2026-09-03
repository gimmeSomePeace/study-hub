package me.gimmesomepeace.studyhub.semester.service

import me.gimmesomepeace.studyhub.common.id.IdGenerator
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
    fun getById(
        id: UUID,
        userId: UUID,
    ): SemesterDetails = semesterRepository
        .findByIdAndOwnerId(id, userId)
        ?.toDetails()
        ?: throw SemesterNotFoundException(id)

    @Transactional(readOnly = true)
    fun list(
        pageable: Pageable,
        userId: UUID,
    ): Page<SemesterListItem> = semesterRepository.findByOwnerId(userId, pageable).map { it.toListItem() }

    @Transactional
    fun create(
        request: SemesterCreateRequest,
        userId: UUID,
    ): SemesterDetails {
        if (!request.endsAt.isAfter(request.startsAt)) {
            throw SemesterValidationException("End date must be after start date")
        }
        val semester = SemesterEntity(
            id = idGenerator.generate(),
            ownerId = userId,
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
        userId: UUID,
    ): SemesterDetails {
        val semester = semesterRepository
            .findByIdAndOwnerId(id, userId)
            ?: throw SemesterNotFoundException(userId)

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
    fun delete(
        id: UUID,
        userId: UUID,
    ) {
        semesterRepository.deleteByIdAndOwnerId(id, userId)
    }
}
