package me.gimmesomepeace.studyhub.core.exception

import jakarta.validation.ValidationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import tools.jackson.databind.exc.InvalidFormatException
import java.time.Instant

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(ex: Exception): ProblemDetail {
        log.error("Unexpected error: ${ex.message}", ex)

        return ProblemDetail
            .forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
            ).apply {
                title = "Internal Server Error"
                setProperty("code", "INTERNAL_ERROR")
                setProperty("timestamp", Instant.now())
            }
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(e: MethodArgumentNotValidException): ProblemDetail {
        val errors = e.bindingResult.fieldErrors.map { fieldError ->
            mapOf(
                "field" to fieldError.field,
                "rejectedValue" to fieldError.rejectedValue?.toString(),
                "message" to fieldError.defaultMessage,
            )
        }

        return ProblemDetail
            .forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Request validation failed",
            ).apply {
                title = "Validation Error"
                setProperty("code", "VALIDATION_ERROR")
                setProperty("errors", errors)
                setProperty("timestamp", Instant.now())
            }
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleNotReadable(e: HttpMessageNotReadableException): ProblemDetail {
        val cause = e.cause

        if (cause is InvalidFormatException) {
            val targetType = cause.targetType

            if (targetType.isEnum) {
                val enumValues = targetType.enumConstants.map { (it as Enum<*>).name }

                return ProblemDetail
                    .forStatusAndDetail(
                        HttpStatus.BAD_REQUEST,
                        "Invalid value for field '${cause.path.joinToString(".") { it.propertyName }}'",
                    ).apply {
                        title = "Invalid Value"
                        setProperty("code", "INVALID_VALUE")
                        setProperty("rejectedValue", cause.value)
                        setProperty("allowedValues", enumValues)
                        setProperty("timestamp", Instant.now())
                    }
            }
        }

        return ProblemDetail
            .forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Request body is malformed",
            ).apply {
                title = "Malformed Request"
                setProperty("code", "MALFORMED_REQUEST")
                setProperty("timestamp", Instant.now())
            }
    }

    @ExceptionHandler(ValidationException::class)
    fun handleValidation(ex: ValidationException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.message).apply {
            title = "Validation error"
            setProperty("code", "VALIDATION_ERROR")
            setProperty("timestamp", Instant.now())
        }
}
