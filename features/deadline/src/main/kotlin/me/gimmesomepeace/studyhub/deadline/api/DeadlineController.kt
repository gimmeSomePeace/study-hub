package me.gimmesomepeace.studyhub.deadline.api

import jakarta.validation.Valid
import me.gimmesomepeace.studyhub.core.user.UserPrincipal
import me.gimmesomepeace.studyhub.deadline.api.create.DeadlineCreateRequest
import me.gimmesomepeace.studyhub.deadline.api.update.DeadlineUpdateRequest
import me.gimmesomepeace.studyhub.deadline.dto.DeadlineDetails
import me.gimmesomepeace.studyhub.deadline.dto.DeadlineListItem
import me.gimmesomepeace.studyhub.deadline.service.DeadlineService
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
@RequestMapping("/api/v1/deadlines")
class DeadlineController(
    private val service: DeadlineService,
) {
    @GetMapping("/{id}")
    fun get(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable id: UUID,
    ): ResponseEntity<DeadlineDetails> {
        val deadline = service.getById(id, userPrincipal.userId)
        return ResponseEntity.ok(deadline)
    }

    @GetMapping
    fun list(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PageableDefault(size = 20, sort = ["dueAt"], direction = Sort.Direction.ASC)
        pageable: Pageable,
    ): Page<DeadlineListItem> = service.list(pageable = pageable, userPrincipal.userId)

    @PostMapping
    fun create(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @Valid @RequestBody request: DeadlineCreateRequest,
    ): ResponseEntity<DeadlineDetails> {
        val createdDeadline = service.create(request, userPrincipal.userId)
        return ResponseEntity
            .created(URI("/api/v1/deadlines/${createdDeadline.id}"))
            .body(createdDeadline)
    }

    @PatchMapping("/{id}")
    fun update(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable id: UUID,
        @Valid @RequestBody request: DeadlineUpdateRequest,
    ): ResponseEntity<DeadlineDetails> {
        val updatedDeadline = service.update(id, request, userPrincipal.userId)
        return ResponseEntity.ok(updatedDeadline)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable id: UUID,
    ) {
        service.delete(id, userPrincipal.userId)
    }

    @PostMapping("/{id}/close")
    fun close(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable id: UUID,
    ): ResponseEntity<DeadlineDetails> {
        val updatedDeadline = service.closeDeadline(id, userPrincipal.userId)
        return ResponseEntity.ok(updatedDeadline)
    }

    @PostMapping("/{id}/cancel")
    fun cancel(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable id: UUID,
    ): ResponseEntity<DeadlineDetails> {
        val updatedDeadline = service.cancelDeadline(id, userPrincipal.userId)
        return ResponseEntity.ok(updatedDeadline)
    }

    @PostMapping("/{id}/reopen")
    fun reopen(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable id: UUID,
    ): ResponseEntity<DeadlineDetails> {
        val updatedDeadline = service.reopenDeadline(id, userPrincipal.userId)
        return ResponseEntity.ok(updatedDeadline)
    }
}
