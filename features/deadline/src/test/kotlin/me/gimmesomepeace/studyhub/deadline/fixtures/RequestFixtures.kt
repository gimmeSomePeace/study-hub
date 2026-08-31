package me.gimmesomepeace.studyhub.deadline.fixtures

import me.gimmesomepeace.studyhub.deadline.api.create.DeadlineCreateRequest
import me.gimmesomepeace.studyhub.deadline.api.update.DeadlineUpdateRequest
import me.gimmesomepeace.studyhub.deadline.dto.DeadlineType
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.time.Instant
import java.util.UUID

fun deadlineCreateRequest(
    subjectId: UUID = deadlineSubjectId(),
    componentId: UUID? = null,
    title: String = "Лабораторная работа 1",
    dueAt: Instant = futureDueAt(),
    type: DeadlineType = DeadlineType.LAB,
    notes: String? = "Сдать через LMS",
) = DeadlineCreateRequest(
    subjectId = subjectId,
    componentId = componentId,
    title = title,
    dueAt = dueAt,
    type = type,
    notes = notes,
)

fun deadlineUpdateRequest(
    subjectId: UUID? = null,
    componentId: UUID? = null,
    title: String? = null,
    dueAt: Instant? = null,
    type: DeadlineType? = null,
    notes: String? = null,
) = DeadlineUpdateRequest(
    subjectId = subjectId,
    componentId = componentId,
    title = title,
    dueAt = dueAt,
    type = type,
    notes = notes,
)

fun deadlinePageable(
    page: Int = 0,
    size: Int = 20,
    sort: Sort = Sort.by(Sort.Direction.ASC, "dueAt"),
): Pageable = PageRequest.of(page, size, sort)
