package me.gimmesomepeace.studyhub.subject.component.fixtures

import me.gimmesomepeace.studyhub.subject.component.api.create.ComponentCreateRequest
import me.gimmesomepeace.studyhub.subject.component.api.update.ComponentUpdateRequest
import me.gimmesomepeace.studyhub.subject.component.dto.ComponentType
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

fun componentCreateRequest(
    type: ComponentType = ComponentType.LECTURE,
    title: String = "Лекция 1: Введение",
    priority: Int = 3,
    notes: String? = "Базовые понятия",
) = ComponentCreateRequest(
    type = type,
    title = title,
    priority = priority,
    notes = notes,
)

fun componentUpdateRequest(
    type: ComponentType? = null,
    title: String? = null,
    priority: Int? = null,
    notes: String? = null,
) = ComponentUpdateRequest(
    type = type,
    title = title,
    priority = priority,
    notes = notes,
)

fun componentPageable(
    page: Int = 0,
    size: Int = 20,
    sort: Sort = Sort.by(Sort.Direction.DESC, "createdAt"),
): Pageable = PageRequest.of(page, size, sort)
