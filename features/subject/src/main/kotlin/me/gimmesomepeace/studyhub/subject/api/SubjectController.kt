package me.gimmesomepeace.studyhub.subject.api

import jakarta.validation.Valid
import me.gimmesomepeace.studyhub.subject.api.create.SubjectCreateRequest
import me.gimmesomepeace.studyhub.subject.api.update.SubjectUpdateRequest
import me.gimmesomepeace.studyhub.subject.dto.SubjectDetails
import me.gimmesomepeace.studyhub.subject.dto.SubjectListItem
import me.gimmesomepeace.studyhub.subject.service.SubjectService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.util.UUID

@RestController
@RequestMapping("/subjects")
class SubjectController(
    private val service: SubjectService,
) {
    @GetMapping("/{id}")
    fun get(
        @PathVariable id: UUID,
    ): ResponseEntity<SubjectDetails> {
        val subject = service.getById(id)
        return ResponseEntity.ok(subject)
    }

    @GetMapping
    fun list(
        @PageableDefault(size = 20, sort = ["createdAt"], direction = Sort.Direction.DESC)
        pageable: Pageable,
    ): Page<SubjectListItem> = service.list(pageable)

    @PostMapping
    fun create(
        @Valid @RequestBody request: SubjectCreateRequest,
    ): ResponseEntity<SubjectDetails> {
        val createdSubject = service.create(request)
        return ResponseEntity
            .created(URI("/subjects/${createdSubject.id}"))
            .body(createdSubject)
    }

    @PatchMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @Valid @RequestBody request: SubjectUpdateRequest,
    ): ResponseEntity<SubjectDetails> {
        val updatedSubject = service.update(id, request)
        return ResponseEntity.ok(updatedSubject)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable id: UUID,
    ) {
        service.delete(id)
    }
}
