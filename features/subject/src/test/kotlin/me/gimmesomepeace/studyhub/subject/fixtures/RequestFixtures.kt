package me.gimmesomepeace.studyhub.subject.fixtures

import me.gimmesomepeace.studyhub.subject.api.create.SubjectCreateRequest
import me.gimmesomepeace.studyhub.subject.api.update.SubjectUpdateRequest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.util.UUID

fun subjectCreateRequest(
    semesterId: UUID = semesterId(),
    name: String = "Алгоритмы",
    code: String? = "CS101",
    teacher: String? = "Иванов И.И.",
    color: String? = "#3B82F6",
) = SubjectCreateRequest(
    semesterId = semesterId,
    name = name,
    code = code,
    teacher = teacher,
    color = color,
)

fun subjectUpdateRequest(
    semesterId: UUID? = null,
    name: String? = null,
    code: String? = null,
    teacher: String? = null,
    color: String? = null,
) = SubjectUpdateRequest(
    semesterId = semesterId,
    name = name,
    code = code,
    teacher = teacher,
    color = color,
)

fun pageable(
    page: Int = 0,
    size: Int = 20,
    sort: Sort = Sort.by(Sort.Direction.DESC, "createdAt"),
): Pageable = PageRequest.of(page, size, sort)
