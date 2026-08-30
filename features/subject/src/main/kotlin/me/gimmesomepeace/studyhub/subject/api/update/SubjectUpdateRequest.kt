package me.gimmesomepeace.studyhub.subject.api.update

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import me.gimmesomepeace.studyhub.common.validation.NotBlankIfPresent
import me.gimmesomepeace.studyhub.subject.constraint.SubjectConstraints
import java.util.UUID

data class SubjectUpdateRequest(
    val semesterId: UUID? = null,

    @field:NotBlankIfPresent("Name must be not blank")
    @field:Size(
        min = SubjectConstraints.NAME_MIN_LENGTH,
        max = SubjectConstraints.NAME_MAX_LENGTH,
        message = "Name length must be between ${SubjectConstraints.NAME_MIN_LENGTH} and" +
            " ${SubjectConstraints.NAME_MAX_LENGTH} characters",
    )
    val name: String?,

    @field:NotBlankIfPresent("Code must be not blank")
    @field:Size(
        min = SubjectConstraints.CODE_MIN_LENGTH,
        max = SubjectConstraints.CODE_MAX_LENGTH,
        message = "Code length must be between ${SubjectConstraints.CODE_MIN_LENGTH} and" +
            " ${SubjectConstraints.CODE_MAX_LENGTH} characters",
    )
    val code: String? = null,

    @field:NotBlankIfPresent("Teacher must be not blank")
    @field:Size(
        max = SubjectConstraints.TEACHER_MAX_LENGTH,
        message = "Teacher must not exceed ${SubjectConstraints.TEACHER_MAX_LENGTH} characters",
    )
    val teacher: String? = null,

    @field:Pattern(
        regexp = "^#[0-9A-Fa-f]{6}$",
        message = "Color must be a valid hex color (e.g. #FF5733)",
    )
    val color: String? = null,
)
