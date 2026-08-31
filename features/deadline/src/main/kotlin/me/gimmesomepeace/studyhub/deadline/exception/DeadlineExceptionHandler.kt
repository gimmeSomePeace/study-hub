package me.gimmesomepeace.studyhub.deadline.exception

import jakarta.validation.ValidationException
import me.gimmesomepeace.studyhub.subject.component.exception.ComponentNotFoundException
import me.gimmesomepeace.studyhub.subject.exception.SubjectNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.Instant

@RestControllerAdvice(basePackages = ["me.gimmesomepeace.studyhub.deadline"])
class DeadlineExceptionHandler {
    @ExceptionHandler(DeadlineNotFoundException::class)
    fun handleDeadlineNotFound(ex: DeadlineNotFoundException): ProblemDetail =
        ProblemDetail.forStatus(HttpStatus.NOT_FOUND).apply {
            title = "Deadline Not Found"
            detail = "Deadline with id '${ex.id}' not found"
            setProperty("code", "DEADLINE_NOT_FOUND")
            setProperty("timestamp", Instant.now())
        }

    @ExceptionHandler(InvalidStatusTransitionException::class)
    fun handleInvalidTransition(ex: InvalidStatusTransitionException): ProblemDetail =
        ProblemDetail.forStatus(HttpStatus.CONFLICT).apply {
            title = "Invalid Status Transition"
            detail = "Cannot transition from '${ex.from}' to '${ex.to}'"
            setProperty("code", "INVALID_STATUS_TRANSITION")
            setProperty("timestamp", Instant.now())
        }

    @ExceptionHandler(SubjectNotFoundException::class)
    fun handleSubjectNotFound(ex: SubjectNotFoundException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.message).apply {
            title = "Subject Not Found"
            setProperty("code", "SUBJECT_NOT_FOUND")
            setProperty("timestamp", Instant.now())
        }

    @ExceptionHandler(ComponentNotFoundException::class)
    fun handleComponentNotFound(ex: ComponentNotFoundException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.message).apply {
            title = "Component Not Found"
            setProperty("code", "COMPONENT_NOT_FOUND")
            setProperty("timestamp", Instant.now())
        }

    @ExceptionHandler(ValidationException::class)
    fun handleValidation(ex: ValidationException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.message).apply {
            title = "Validation Error"
            setProperty("code", "DEADLINE_VALIDATION_ERROR")
            setProperty("timestamp", Instant.now())
        }
}
