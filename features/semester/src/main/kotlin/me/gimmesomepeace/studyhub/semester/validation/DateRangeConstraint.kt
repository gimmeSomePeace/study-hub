package me.gimmesomepeace.studyhub.semester.validation

import java.time.LocalDate

interface DateRangeConstraint {
    val startDate: LocalDate?
    val endDate: LocalDate?
}
