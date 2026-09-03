package me.gimmesomepeace.studyhub.common.exception

import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class DebugExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(e: MethodArgumentNotValidException): ResponseEntity<Map<String, Any?>> {
        val errors = e.bindingResult.allErrors.map { error ->
            mapOf(
                "object" to (error as? FieldError)?.field,
                "message" to error.defaultMessage,
                "code" to error.code,
            )
        }
        return ResponseEntity.badRequest().body(mapOf("errors" to errors))
    }
}
