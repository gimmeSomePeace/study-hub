package me.gimmesomepeace.studyhub.subject.component.exception

import me.gimmesomepeace.studyhub.subject.component.api.ComponentController
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.Instant

@RestControllerAdvice(assignableTypes = [ComponentController::class])
class ComponentExceptionHandler {
    @ExceptionHandler(ComponentNotFoundException::class)
    fun handleComponentNotFound(ex: ComponentNotFoundException): ProblemDetail =
        ProblemDetail.forStatus(HttpStatus.NOT_FOUND).apply {
            title = "Component Not Found"
            detail = "Component with id ${ex.id} not found"
            setProperty("code", "COMPONENT_NOT_FOUND")
            setProperty("timestamp", Instant.now())
        }

}
