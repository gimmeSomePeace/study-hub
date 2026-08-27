package me.gimmesomepeace.studyhub.semester.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class DateRangeValidator : ConstraintValidator<DateRange, DateRangeConstraint> {
    override fun isValid(
        value: DateRangeConstraint?,
        context: ConstraintValidatorContext?,
    ): Boolean {
        if (value == null) return true

        val start = value.startDate ?: return true
        val end = value.endDate ?: return true

        return end.isAfter(start)
    }
}
