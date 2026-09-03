package me.gimmesomepeace.studyhub.semester.api

import jakarta.validation.Valid
import me.gimmesomepeace.studyhub.common.user.UserPrincipal
import me.gimmesomepeace.studyhub.semester.api.create.SemesterCreateRequest
import me.gimmesomepeace.studyhub.semester.api.update.SemesterUpdateRequest
import me.gimmesomepeace.studyhub.semester.dto.SemesterDetails
import me.gimmesomepeace.studyhub.semester.dto.SemesterListItem
import me.gimmesomepeace.studyhub.semester.service.SemesterService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
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
@RequestMapping("/semesters")
class SemesterController(
    private val service: SemesterService,
) {
    @GetMapping("/{id}")
    fun get(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable id: UUID,
    ): ResponseEntity<SemesterDetails> {
        val semester = service.getById(id, userPrincipal.userId)
        return ResponseEntity.ok(semester)
    }

    @GetMapping
    fun list(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PageableDefault(size = 20, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): Page<SemesterListItem> = service.list(pageable, userPrincipal.userId)

    @PostMapping
    fun create(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @Valid @RequestBody request: SemesterCreateRequest,
    ): ResponseEntity<SemesterDetails> {
        val createdSemester = service.create(request, userPrincipal.userId)
        return ResponseEntity.created(URI("/semesters/${createdSemester.id}")).body(createdSemester)
    }

    @PatchMapping("/{id}")
    fun update(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable id: UUID,
        @Valid @RequestBody request: SemesterUpdateRequest,
    ): ResponseEntity<SemesterDetails> {
        val updatedSemester = service.update(id, request, userPrincipal.userId)
        return ResponseEntity.ok(updatedSemester)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable id: UUID,
    ) {
        service.delete(id, userPrincipal.userId)
    }
}
