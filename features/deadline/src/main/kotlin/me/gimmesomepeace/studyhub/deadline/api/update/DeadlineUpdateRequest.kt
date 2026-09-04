package me.gimmesomepeace.studyhub.deadline.api.update

import jakarta.validation.constraints.Size
import me.gimmesomepeace.studyhub.core.validation.NotBlankIfPresent
import me.gimmesomepeace.studyhub.deadline.DeadlineConstraints
import me.gimmesomepeace.studyhub.deadline.dto.DeadlineType
import java.time.Instant
import java.util.UUID

data class DeadlineUpdateRequest(
    val subjectId: UUID? = null,

    val componentId: UUID? = null,

    @field:NotBlankIfPresent(message = "Title must not be blank")
    @field:Size(
        min = DeadlineConstraints.TITLE_MIN_LENGTH,
        max = DeadlineConstraints.TITLE_MAX_LENGTH,
        message = "Title must be between ${DeadlineConstraints.TITLE_MIN_LENGTH} and " +
            "${DeadlineConstraints.TITLE_MAX_LENGTH} characters",
    )
    val title: String? = null,
    val dueAt: Instant? = null,
    val type: DeadlineType? = null,

    @field:Size(
        max = DeadlineConstraints.NOTES_MAX_LENGTH,
        message = "Notes must not exceed ${DeadlineConstraints.NOTES_MAX_LENGTH} characters",
    )
    val notes: String? = null,
)
