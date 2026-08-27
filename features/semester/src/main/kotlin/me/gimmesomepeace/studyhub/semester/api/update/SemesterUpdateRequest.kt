package me.gimmesomepeace.studyhub.semester.api.update

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import me.gimmesomepeace.studyhub.semester.SemesterConstraints
import me.gimmesomepeace.studyhub.semester.validation.DateRange
import me.gimmesomepeace.studyhub.semester.validation.DateRangeConstraint
import java.time.LocalDate

@DateRange
data class SemesterUpdateRequest(
    @field:NotBlank(message = "Name must be not blank")
    @field:Size(
        min = SemesterConstraints.NAME_MIN_LENGTH,
        max = SemesterConstraints.NAME_MAX_LENGTH,
        message = "Name must be between ${SemesterConstraints.NAME_MIN_LENGTH} and" +
            " ${SemesterConstraints.NAME_MAX_LENGTH} characters",
    ) val name: String? = null,

    val startsAt: LocalDate? = null,
    val endsAt: LocalDate? = null,
) : DateRangeConstraint {
    override val startDate: LocalDate? get() = startsAt
    override val endDate: LocalDate? get() = endsAt
}
