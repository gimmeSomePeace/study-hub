package me.gimmesomepeace.studyhub.subject.component.dto

import io.swagger.v3.oas.annotations.media.Schema

@Suppress("unused")
@Schema(description = "Тип компонента предмета")
enum class ComponentType {
    @Schema(description = "Лекция")
    LECTURE,

    @Schema(description = "Лабораторное занятие")
    LAB,

    @Schema(description = "Практическое занятие")
    PRACTICE,

    @Schema(description = "Другое")
    OTHER,
}
