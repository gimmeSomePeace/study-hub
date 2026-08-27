package me.gimmesomepeace.studyhub.semester.api

import me.gimmesomepeace.studyhub.semester.api.create.SemesterCreateRequest
import me.gimmesomepeace.studyhub.semester.api.update.SemesterUpdateRequest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.time.LocalDate

fun semesterCreateRequest(
    name: String = "DEFAULT SEMESTER NAME",
    startsAt: LocalDate = LocalDate.of(2020, 1, 1),
    endsAt: LocalDate = LocalDate.of(2021, 1, 1),
) = SemesterCreateRequest(
    name = name,
    startsAt = startsAt,
    endsAt = endsAt,
)

fun semesterUpdateRequest(
    name: String? = null,
    startsAt: LocalDate? = null,
    endsAt: LocalDate? = null,
) = SemesterUpdateRequest(
    name = name,
    startsAt = startsAt,
    endsAt = endsAt,
)

fun pageable(
    page: Int = 0,
    size: Int = 20,
    sort: Sort = Sort.by(Sort.Direction.DESC, "createdAt"),
): Pageable = PageRequest.of(page, size, sort)
