package me.gimmesomepeace.studyhub.subject.api

import me.gimmesomepeace.studyhub.subject.exception.NotFoundException
import me.gimmesomepeace.studyhub.subject.exception.SemesterNotFoundException
import me.gimmesomepeace.studyhub.subject.exception.ValidationException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.Instant

@RestControllerAdvice(assignableTypes = [SubjectController::class])
class SubjectExceptionHandler {
    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException): ProblemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND).apply {
        title = "Subject Not Found"
        detail = "Subject with id ${ex.id} not found"
        setProperty("code", "SUBJECT_NOT_FOUND")
        setProperty("timestamp", Instant.now())
    }

    @ExceptionHandler(ValidationException::class)
    fun handleValidation(ex: ValidationException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.message).apply {
            title = "Validation Error"
            setProperty("code", "SUBJECT_VALIDATION_ERROR")
            setProperty("timestamp", Instant.now())
        }

    @ExceptionHandler(SemesterNotFoundException::class)
    fun handleSemesterNotFound(ex: SemesterNotFoundException): ProblemDetail =
        ProblemDetail.forStatus(HttpStatus.NOT_FOUND).apply {
            title = "Semester Not Found"
            detail = "Semester with id '${ex.semesterId}' not found"
            setProperty("code", "SEMESTER_NOT_FOUND")
            setProperty("timestamp", Instant.now())
        }
}
