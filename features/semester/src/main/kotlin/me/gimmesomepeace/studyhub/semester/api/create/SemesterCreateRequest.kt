package me.gimmesomepeace.studyhub.semester.api.create

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import me.gimmesomepeace.studyhub.semester.SemesterConstraints
import me.gimmesomepeace.studyhub.semester.validation.DateRange
import me.gimmesomepeace.studyhub.semester.validation.DateRangeConstraint
import java.time.LocalDate

@DateRange
data class SemesterCreateRequest(
    @field:NotBlank(message = "Name must be not blank")
    @field:Size(
        min = SemesterConstraints.NAME_MIN_LENGTH,
        max = SemesterConstraints.NAME_MAX_LENGTH,
        message = "Name must be between ${SemesterConstraints.NAME_MIN_LENGTH} and" +
            " ${SemesterConstraints.NAME_MAX_LENGTH} characters",
    )
    val name: String,

    val startsAt: LocalDate,
    val endsAt: LocalDate,
) : DateRangeConstraint {
    override val startDate: LocalDate get() = startsAt
    override val endDate: LocalDate get() = endsAt
}
