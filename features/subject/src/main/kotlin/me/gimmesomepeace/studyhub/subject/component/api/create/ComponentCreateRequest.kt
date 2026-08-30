package me.gimmesomepeace.studyhub.subject.component.api.create

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import me.gimmesomepeace.studyhub.subject.component.constraint.ComponentConstraints
import me.gimmesomepeace.studyhub.subject.component.dto.ComponentType

data class ComponentCreateRequest(
    @field:NotNull(message = "Type is required")
    val type: ComponentType,

    @field:NotBlank(message = "Title must not be blank")
    @field:Size(
        min = ComponentConstraints.TITLE_MIN_LENGTH,
        max = ComponentConstraints.TITLE_MAX_LENGTH,
        message = "Title must be between ${ComponentConstraints.TITLE_MIN_LENGTH} and " +
            "${ComponentConstraints.TITLE_MAX_LENGTH} characters",
    )
    val title: String,

    @field:Min(
        ComponentConstraints.PRIORITY_MIN,
        message = "Priority must be at least ${ComponentConstraints.PRIORITY_MIN}",
    )
    @field:Max(
        ComponentConstraints.PRIORITY_MAX,
        message = "Priority must be at most ${ComponentConstraints.PRIORITY_MAX}",
    )
    val priority: Int = ComponentConstraints.PRIORITY_DEFAULT,

    @field:Size(
        max = ComponentConstraints.NOTES_MAX_LENGTH,
        message = "Notes must not exceed ${ComponentConstraints.NOTES_MAX_LENGTH} characters",
    )
    val notes: String? = null,
)
