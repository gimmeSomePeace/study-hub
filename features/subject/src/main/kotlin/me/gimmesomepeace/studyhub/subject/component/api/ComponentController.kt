package me.gimmesomepeace.studyhub.subject.component.api

import jakarta.validation.Valid
import me.gimmesomepeace.studyhub.common.user.UserPrincipal
import me.gimmesomepeace.studyhub.subject.component.api.create.ComponentCreateRequest
import me.gimmesomepeace.studyhub.subject.component.api.update.ComponentUpdateRequest
import me.gimmesomepeace.studyhub.subject.component.dto.ComponentDetails
import me.gimmesomepeace.studyhub.subject.component.dto.ComponentListItem
import me.gimmesomepeace.studyhub.subject.component.service.ComponentService
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
@RequestMapping("/subjects/{subjectId}/components")
class ComponentController(
    private val service: ComponentService,
) {
    @GetMapping("/{id}")
    fun get(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable subjectId: UUID,
        @PathVariable id: UUID,
    ): ResponseEntity<ComponentDetails> {
        val component = service.getById(subjectId, id, userPrincipal.userId)
        return ResponseEntity.ok(component)
    }

    @GetMapping
    fun list(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable subjectId: UUID,
        @PageableDefault(size = 20, sort = ["createdAt"], direction = Sort.Direction.DESC)
        pageable: Pageable,
    ): Page<ComponentListItem> = service.list(subjectId, pageable, userPrincipal.userId)

    @PostMapping
    fun create(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable subjectId: UUID,
        @Valid @RequestBody request: ComponentCreateRequest,
    ): ResponseEntity<ComponentDetails> {
        val createdComponent = service.create(subjectId, request, userPrincipal.userId)
        return ResponseEntity
            .created(URI("/subjects/$subjectId/components/${createdComponent.id}"))
            .body(createdComponent)
    }

    @PatchMapping("/{id}")
    fun update(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable subjectId: UUID,
        @PathVariable id: UUID,
        @Valid @RequestBody request: ComponentUpdateRequest,
    ): ResponseEntity<ComponentDetails> {
        val updatedComponent = service.update(subjectId, id, request, userPrincipal.userId)
        return ResponseEntity.ok(updatedComponent)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable subjectId: UUID,
        @PathVariable id: UUID,
    ) {
        service.delete(subjectId, id, userPrincipal.userId)
    }
}
