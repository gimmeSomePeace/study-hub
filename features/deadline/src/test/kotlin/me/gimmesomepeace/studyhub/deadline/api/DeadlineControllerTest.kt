package me.gimmesomepeace.studyhub.deadline.api

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import me.gimmesomepeace.studyhub.deadline.TestSecurityConfig
import me.gimmesomepeace.studyhub.deadline.dto.DeadlineListItem
import me.gimmesomepeace.studyhub.deadline.dto.DeadlineStatus
import me.gimmesomepeace.studyhub.deadline.dto.DeadlineType
import me.gimmesomepeace.studyhub.deadline.exception.DeadlineNotFoundException
import me.gimmesomepeace.studyhub.deadline.exception.InvalidStatusTransitionException
import me.gimmesomepeace.studyhub.deadline.fixtures.authenticateAs
import me.gimmesomepeace.studyhub.deadline.fixtures.deadlineComponentId
import me.gimmesomepeace.studyhub.deadline.fixtures.deadlineCreateRequest
import me.gimmesomepeace.studyhub.deadline.fixtures.deadlineDetails
import me.gimmesomepeace.studyhub.deadline.fixtures.deadlineId
import me.gimmesomepeace.studyhub.deadline.fixtures.deadlineListItem
import me.gimmesomepeace.studyhub.deadline.fixtures.deadlineSubjectId
import me.gimmesomepeace.studyhub.deadline.fixtures.deadlineUpdateRequest
import me.gimmesomepeace.studyhub.deadline.fixtures.userId
import me.gimmesomepeace.studyhub.deadline.service.DeadlineService
import me.gimmesomepeace.studyhub.subject.component.exception.ComponentNotFoundException
import me.gimmesomepeace.studyhub.subject.exception.SubjectNotFoundException
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import tools.jackson.databind.ObjectMapper

@WebMvcTest(DeadlineController::class)
@Import(TestSecurityConfig::class)
class DeadlineControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var service: DeadlineService

    private val deadlineId = deadlineId()
    private val subjectId = deadlineSubjectId()
    private val userId = userId()

    @Nested
    inner class GetById {
        @Test
        fun `should return deadline with all fields`() {
            val details = deadlineDetails(
                id = deadlineId,
                subjectId = subjectId,
                title = "Лабораторная работа 1",
                type = DeadlineType.LAB,
                status = DeadlineStatus.OPEN,
                notes = "Сдать через LMS",
            )

            every { service.getById(deadlineId, userId) } returns details

            mvc
                .get("/deadlines/{id}", deadlineId) {
                    accept = MediaType.APPLICATION_JSON
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.id") { value(deadlineId.toString()) }
                    jsonPath("$.subjectId") { value(subjectId.toString()) }
                    jsonPath("$.title") { value(details.title) }
                    jsonPath("$.type") { value(details.type.name) }
                    jsonPath("$.status") { value(details.status.name) }
                    jsonPath("$.notes") { value(details.notes) }
                    jsonPath("$.dueAt") { exists() }
                    jsonPath("$.createdAt") { exists() }
                    jsonPath("$.updatedAt") { exists() }
                }
        }

        @Test
        fun `should return 404 when deadline not found`() {
            every { service.getById(deadlineId, userId) } throws DeadlineNotFoundException(deadlineId)

            mvc
                .get("/deadlines/{id}", deadlineId) {
                    accept = MediaType.APPLICATION_JSON
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isNotFound() }
                    jsonPath("$.code") { value("DEADLINE_NOT_FOUND") }
                }
        }
    }

    @Nested
    inner class List {
        @Test
        fun `should return page of deadlines`() {
            val items = listOf(
                deadlineListItem(title = "Лаба 1"),
                deadlineListItem(title = "Лаба 2"),
            )
            val page: Page<DeadlineListItem> = PageImpl(items)

            every { service.list(any<Pageable>(), userId) } returns page

            mvc
                .get("/deadlines") {
                    accept = MediaType.APPLICATION_JSON
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.content") { isArray() }
                    jsonPath("$.content.length()") { value(2) }
                    jsonPath("$.content[0].title") { value("Лаба 1") }
                    jsonPath("$.content[1].title") { value("Лаба 2") }
                    jsonPath("$.totalElements") { value(2) }
                }
        }

        @Test
        fun `should return empty page when no deadlines`() {
            val page: Page<DeadlineListItem> = PageImpl(emptyList())

            every { service.list(any<Pageable>(), userId) } returns page

            mvc
                .get("/deadlines") {
                    accept = MediaType.APPLICATION_JSON
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.content") { isArray() }
                    jsonPath("$.content.length()") { value(0) }
                }
        }
    }

    @Nested
    inner class Create {
        @Test
        fun `should create deadline and return 201 with location header`() {
            val request = deadlineCreateRequest(
                subjectId = subjectId,
                title = "Лабораторная работа 1",
                type = DeadlineType.LAB,
            )

            val created = deadlineDetails(
                id = deadlineId,
                subjectId = request.subjectId,
                title = request.title,
                type = request.type,
                status = DeadlineStatus.OPEN,
            )

            every { service.create(request, userId) } returns created

            mvc
                .post("/deadlines") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isCreated() }
                    header { string("Location", "/deadlines/$deadlineId") }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.id") { value(deadlineId.toString()) }
                    jsonPath("$.subjectId") { value(subjectId.toString()) }
                    jsonPath("$.title") { value(request.title) }
                    jsonPath("$.type") { value(request.type.name) }
                    jsonPath("$.status") { exists() }
                }

            verify { service.create(request, userId) }
        }

        @Test
        fun `should return 404 when subject not found`() {
            val request = deadlineCreateRequest(subjectId = subjectId)

            every { service.create(request, userId) } throws SubjectNotFoundException(subjectId)

            mvc
                .post("/deadlines") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isNotFound() }
                    jsonPath("$.code") { value("SUBJECT_NOT_FOUND") }
                }
        }

        @Test
        fun `should return 404 when component not found`() {
            val componentId = deadlineComponentId()
            val request = deadlineCreateRequest(subjectId = subjectId, componentId = componentId)

            every { service.create(request, userId) } throws ComponentNotFoundException(componentId)

            mvc
                .post("/deadlines") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isNotFound() }
                    jsonPath("$.code") { value("COMPONENT_NOT_FOUND") }
                }

            verify { service.create(request, userId) }
        }

        @ParameterizedTest
        @MethodSource(
            "me.gimmesomepeace.studyhub.deadline.fixtures.DeadlineTestData#invalidCreateDeadlineRequests",
        )
        fun `should return 400 when request is invalid`(requestBody: Map<String, Any?>) {
            mvc
                .post("/deadlines") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(requestBody)
                }.andExpect {
                    status { isBadRequest() }
                }
        }
    }

    @Nested
    inner class Update {
        @Test
        fun `should update deadline and return 200`() {
            val newTitle = "Лабораторная работа 1: Обновлённое название"
            val request = deadlineUpdateRequest(title = newTitle)

            val updated = deadlineDetails(
                id = deadlineId,
                subjectId = subjectId,
                title = newTitle,
            )

            every { service.update(deadlineId, request, userId) } returns updated

            mvc
                .patch("/deadlines/{id}", deadlineId) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.id") { value(deadlineId.toString()) }
                    jsonPath("$.title") { value(request.title) }
                }

            verify { service.update(deadlineId, request, userId) }
        }

        @Test
        fun `should return 404 when deadline not found`() {
            val request = deadlineUpdateRequest(title = "Лабораторная работа 1: Обновлённое название")

            every { service.update(deadlineId, request, userId) } throws DeadlineNotFoundException(deadlineId)

            mvc
                .patch("/deadlines/{id}", deadlineId) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isNotFound() }
                    jsonPath("$.code") { value("DEADLINE_NOT_FOUND") }
                }
        }

        @ParameterizedTest
        @MethodSource(
            "me.gimmesomepeace.studyhub.deadline.fixtures.DeadlineTestData#invalidUpdateDeadlineRequests",
        )
        fun `should return 400 when request is invalid`(requestBody: Map<String, Any?>) {
            mvc
                .patch("/deadlines/{id}", deadlineId) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(requestBody)
                }.andExpect {
                    status { isBadRequest() }
                }
        }
    }

    @Nested
    inner class Delete {
        @Test
        fun `should delete deadline and return 204`() {
            every { service.delete(deadlineId, userId) } just Runs

            mvc
                .delete("/deadlines/{id}", deadlineId) {
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isNoContent() }
                }

            verify { service.delete(deadlineId, userId) }
        }
    }

    @Nested
    inner class Close {
        @Test
        fun `should close deadline and return 200`() {
            val updated = deadlineDetails(
                id = deadlineId,
                status = DeadlineStatus.CLOSED,
            )

            every { service.closeDeadline(deadlineId, userId) } returns updated

            mvc
                .post("/deadlines/{id}/close", deadlineId) {
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.id") { value(deadlineId.toString()) }
                    jsonPath("$.status") { value("CLOSED") }
                }

            verify { service.closeDeadline(deadlineId, userId) }
        }

        @Test
        fun `should return 404 when deadline not found`() {
            every { service.closeDeadline(deadlineId, userId) } throws DeadlineNotFoundException(deadlineId)

            mvc
                .post("/deadlines/{id}/close", deadlineId) {
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isNotFound() }
                    jsonPath("$.code") { value("DEADLINE_NOT_FOUND") }
                }

            verify { service.closeDeadline(deadlineId, userId) }
        }

        @Test
        fun `should return 409 when transition not allowed`() {
            every { service.closeDeadline(deadlineId, userId) } throws InvalidStatusTransitionException(
                DeadlineStatus.CANCELLED,
                DeadlineStatus.CLOSED,
            )

            mvc
                .post("/deadlines/{id}/close", deadlineId) {
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isConflict() }
                    jsonPath("$.code") { value("INVALID_STATUS_TRANSITION") }
                }
        }
    }

    @Nested
    inner class Cancel {
        @Test
        fun `should cancel deadline and return 200`() {
            val updated = deadlineDetails(
                id = deadlineId,
                status = DeadlineStatus.CANCELLED,
            )

            every { service.cancelDeadline(deadlineId, userId) } returns updated

            mvc
                .post("/deadlines/{id}/cancel", deadlineId) {
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.id") { value(deadlineId.toString()) }
                    jsonPath("$.status") { value("CANCELLED") }
                }

            verify { service.cancelDeadline(deadlineId, userId) }
        }

        @Test
        fun `should return 404 when deadline not found`() {
            every { service.cancelDeadline(deadlineId, userId) } throws DeadlineNotFoundException(deadlineId)

            mvc
                .post("/deadlines/{id}/cancel", deadlineId) {
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isNotFound() }
                    jsonPath("$.code") { value("DEADLINE_NOT_FOUND") }
                }

            verify { service.cancelDeadline(deadlineId, userId) }
        }

        @Test
        fun `should return 409 when transition not allowed`() {
            every { service.cancelDeadline(deadlineId, userId) } throws InvalidStatusTransitionException(
                DeadlineStatus.CLOSED,
                DeadlineStatus.CANCELLED,
            )

            mvc
                .post("/deadlines/{id}/cancel", deadlineId) {
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isConflict() }
                    jsonPath("$.code") { value("INVALID_STATUS_TRANSITION") }
                }

            verify { service.cancelDeadline(deadlineId, userId) }
        }
    }

    @Nested
    inner class Reopen {
        @Test
        fun `should reopen deadline and return 200`() {
            val updated = deadlineDetails(
                id = deadlineId,
                status = DeadlineStatus.OPEN,
            )

            every { service.reopenDeadline(deadlineId, userId) } returns updated

            mvc
                .post("/deadlines/{id}/reopen", deadlineId) {
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.id") { value(deadlineId.toString()) }
                    jsonPath("$.status") { value("OPEN") }
                }

            verify { service.reopenDeadline(deadlineId, userId) }
        }

        @Test
        fun `should return 404 when deadline not found`() {
            every { service.reopenDeadline(deadlineId, userId) } throws DeadlineNotFoundException(deadlineId)

            mvc
                .post("/deadlines/{id}/reopen", deadlineId) {
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isNotFound() }
                    jsonPath("$.code") { value("DEADLINE_NOT_FOUND") }
                }

            verify { service.reopenDeadline(deadlineId, userId) }
        }

        @Test
        fun `should return 409 when transition not allowed`() {
            every { service.reopenDeadline(deadlineId, userId) } throws InvalidStatusTransitionException(
                DeadlineStatus.CANCELLED,
                DeadlineStatus.OPEN,
            )

            mvc
                .post("/deadlines/{id}/reopen", deadlineId) {
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isConflict() }
                    jsonPath("$.code") { value("INVALID_STATUS_TRANSITION") }
                }

            verify { service.reopenDeadline(deadlineId, userId) }
        }
    }
}
