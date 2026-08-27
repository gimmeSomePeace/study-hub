package me.gimmesomepeace.studyhub.semester.exception

import me.gimmesomepeace.studyhub.semester.api.SemesterController
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.Instant

@RestControllerAdvice(assignableTypes = [SemesterController::class])
class SemesterExceptionHandler {
    @ExceptionHandler(SemesterNotFoundException::class)
    fun handleNotFound(e: SemesterNotFoundException) = ProblemDetail.forStatus(HttpStatus.NOT_FOUND).apply {
        title = "Semester not found"
        detail = "Semester with id ${e.semesterId} not found"
        setProperty("code", "SEMESTER_NOT_FOUND")
        setProperty("timestamp", Instant.now())
    }

    @ExceptionHandler(SemesterValidationException::class)
    fun handleValidation(ex: SemesterValidationException) = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST).apply {
        title = "Validation Error"
        detail = ex.message
        setProperty("code", "SEMESTER_VALIDATION_ERROR")
        setProperty("timestamp", Instant.now())
    }
}
