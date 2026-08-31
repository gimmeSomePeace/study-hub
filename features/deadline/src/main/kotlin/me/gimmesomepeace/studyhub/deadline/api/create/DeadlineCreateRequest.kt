package me.gimmesomepeace.studyhub.deadline.api.create

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import me.gimmesomepeace.studyhub.deadline.DeadlineConstraints
import me.gimmesomepeace.studyhub.deadline.dto.DeadlineType
import java.time.Instant
import java.util.UUID

data class DeadlineCreateRequest(
    @field:NotNull(message = "Subject ID is required")
    val subjectId: UUID,

    val componentId: UUID? = null,

    @field:NotBlank(message = "Title must not be blank")
    @field:Size(
        min = DeadlineConstraints.TITLE_MIN_LENGTH,
        max = DeadlineConstraints.TITLE_MAX_LENGTH,
        message = "Title must be between ${DeadlineConstraints.TITLE_MIN_LENGTH} and " +
            "${DeadlineConstraints.TITLE_MAX_LENGTH} characters",
    )
    val title: String,

    val type: DeadlineType,

    @field:NotNull(message = "Due date is required")
    val dueAt: Instant,

    @field:Size(
        max = DeadlineConstraints.NOTES_MAX_LENGTH,
        message = "Notes must not exceed ${DeadlineConstraints.NOTES_MAX_LENGTH} characters",
    )
    val notes: String? = null,
)
