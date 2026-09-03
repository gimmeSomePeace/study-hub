package me.gimmesomepeace.studyhub.subject.component.api

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import me.gimmesomepeace.studyhub.subject.TestSecurityConfig
import me.gimmesomepeace.studyhub.subject.component.dto.ComponentListItem
import me.gimmesomepeace.studyhub.subject.component.dto.ComponentType
import me.gimmesomepeace.studyhub.subject.component.exception.ComponentNotFoundException
import me.gimmesomepeace.studyhub.subject.component.fixtures.componentCreateRequest
import me.gimmesomepeace.studyhub.subject.component.fixtures.componentDetails
import me.gimmesomepeace.studyhub.subject.component.fixtures.componentId
import me.gimmesomepeace.studyhub.subject.component.fixtures.componentListItem
import me.gimmesomepeace.studyhub.subject.component.fixtures.componentUpdateRequest
import me.gimmesomepeace.studyhub.subject.component.service.ComponentService
import me.gimmesomepeace.studyhub.subject.exception.SubjectExceptionHandler
import me.gimmesomepeace.studyhub.subject.exception.SubjectNotFoundException
import me.gimmesomepeace.studyhub.subject.fixtures.authenticateAs
import me.gimmesomepeace.studyhub.subject.fixtures.subjectId
import me.gimmesomepeace.studyhub.subject.fixtures.userId
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

@WebMvcTest(ComponentController::class)
@Import(SubjectExceptionHandler::class, TestSecurityConfig::class)
class ComponentControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var service: ComponentService

    private val subjectId = subjectId()
    private val componentId = componentId()
    private val userId = userId()

    @Nested
    inner class GetById {
        @Test
        fun `should return component with all fields`() {
            val details = componentDetails(
                id = componentId,
                subjectId = subjectId,
                type = ComponentType.LECTURE,
                title = "Лекция 1: Введение в алгоритмы",
                priority = 5,
                notes = "Базовые понятия",
            )

            every { service.getById(subjectId, componentId, userId) } returns details

            mvc
                .get("/subjects/{subjectId}/components/{id}", subjectId, componentId) {
                    accept = MediaType.APPLICATION_JSON
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.id") { value(componentId.toString()) }
                    jsonPath("$.subjectId") { value(subjectId.toString()) }
                    jsonPath("$.type") { value(details.type.name) }
                    jsonPath("$.title") { value(details.title) }
                    jsonPath("$.priority") { value(details.priority) }
                    jsonPath("$.notes") { value(details.notes) }
                    jsonPath("$.createdAt") { exists() }
                    jsonPath("$.updatedAt") { exists() }
                }
        }

        @Test
        fun `should return 404 when subject not found`() {
            every { service.getById(subjectId, componentId, userId) } throws SubjectNotFoundException(subjectId)

            mvc
                .get("/subjects/{subjectId}/components/{id}", subjectId, componentId) {
                    accept = MediaType.APPLICATION_JSON
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isNotFound() }
                    jsonPath("$.code") { value("SUBJECT_NOT_FOUND") }
                }
        }

        @Test
        fun `should return 404 when component not found`() {
            every { service.getById(subjectId, componentId, userId) } throws ComponentNotFoundException(componentId)

            mvc
                .get("/subjects/{subjectId}/components/{id}", subjectId, componentId) {
                    accept = MediaType.APPLICATION_JSON
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isNotFound() }
                    jsonPath("$.code") { value("COMPONENT_NOT_FOUND") }
                }
        }
    }

    @Nested
    inner class List {
        @Test
        fun `should return page of components`() {
            val items = listOf(
                componentListItem(title = "Лекция 1"),
                componentListItem(title = "Лаба 1"),
            )
            val page: Page<ComponentListItem> = PageImpl(items)

            every { service.list(subjectId, any<Pageable>(), userId) } returns page

            mvc
                .get("/subjects/{subjectId}/components", subjectId) {
                    accept = MediaType.APPLICATION_JSON
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.content") { isArray() }
                    jsonPath("$.content.length()") { value(2) }
                    jsonPath("$.content[0].title") { value("Лекция 1") }
                    jsonPath("$.content[1].title") { value("Лаба 1") }
                    jsonPath("$.totalElements") { value(2) }
                }
        }

        @Test
        fun `should return empty page when no components`() {
            val page: Page<ComponentListItem> = PageImpl(emptyList())

            every { service.list(subjectId, any<Pageable>(), userId) } returns page

            mvc
                .get("/subjects/{subjectId}/components", subjectId) {
                    accept = MediaType.APPLICATION_JSON
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.content") { isArray() }
                    jsonPath("$.content.length()") { value(0) }
                }
        }

        @Test
        fun `should return 404 when subject not found`() {
            every { service.list(subjectId, any<Pageable>(), userId) } throws SubjectNotFoundException(subjectId)

            mvc
                .get("/subjects/{subjectId}/components", subjectId) {
                    accept = MediaType.APPLICATION_JSON
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isNotFound() }
                    jsonPath("$.code") { value("SUBJECT_NOT_FOUND") }
                }
        }
    }

    @Nested
    inner class Create {
        @Test
        fun `should create component and return 201 with location header`() {
            val request = componentCreateRequest(
                type = ComponentType.LECTURE,
                title = "Лекция 1: Введение",
                priority = 5,
                notes = "Базовые понятия",
            )

            val created = componentDetails(
                id = componentId,
                subjectId = subjectId,
                type = request.type,
                title = request.title,
                priority = request.priority,
                notes = request.notes,
            )

            every { service.create(subjectId, request, userId) } returns created

            mvc
                .post("/subjects/{subjectId}/components", subjectId) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isCreated() }
                    header { string("Location", "/subjects/$subjectId/components/$componentId") }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.id") { value(componentId.toString()) }
                    jsonPath("$.subjectId") { value(subjectId.toString()) }
                }

            verify { service.create(subjectId, request, userId) }
        }

        @Test
        fun `should return 404 when subject not found`() {
            val request = componentCreateRequest(title = "Лекция 1: Введение")

            every { service.create(subjectId, request, userId) } throws SubjectNotFoundException(subjectId)

            mvc
                .post("/subjects/{subjectId}/components", subjectId) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isNotFound() }
                    jsonPath("$.code") { value("SUBJECT_NOT_FOUND") }
                }
        }

        @ParameterizedTest
        @MethodSource(
            "me.gimmesomepeace.studyhub.subject.component.fixtures.ComponentTestData#invalidCreateComponentRequests",
        )
        fun `should return 400 when request is invalid`(requestBody: Map<String, Any?>) {
            mvc
                .post("/subjects/{subjectId}/components", subjectId) {
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
        fun `should update component and return 200`() {
            val request = componentUpdateRequest(title = "Лекция 1: Обновлённое название")

            val updated = componentDetails(
                id = componentId,
                subjectId = subjectId,
                title = "Лекция 1: Обновлённое название",
            )

            every { service.update(subjectId, componentId, request, userId) } returns updated

            mvc
                .patch("/subjects/{subjectId}/components/{id}", subjectId, componentId) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.id") { value(componentId.toString()) }
                    jsonPath("$.title") { value("Лекция 1: Обновлённое название") }
                }

            verify { service.update(subjectId, componentId, request, userId) }
        }

        @Test
        fun `should return 404 when subject not found`() {
            val request = componentUpdateRequest(title = "Лекция 1: Обновлённое название")

            every { service.update(subjectId, componentId, request, userId) } throws SubjectNotFoundException(subjectId)

            mvc
                .patch("/subjects/{subjectId}/components/{id}", subjectId, componentId) {
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
            val request = componentUpdateRequest(title = "Лекция 1: Обновлённое название")

            every { service.update(subjectId, componentId, request, userId) } throws ComponentNotFoundException(
                componentId,
            )

            mvc
                .patch("/subjects/{subjectId}/components/{id}", subjectId, componentId) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isNotFound() }
                    jsonPath("$.code") { value("COMPONENT_NOT_FOUND") }
                }
        }

        @ParameterizedTest
        @MethodSource(
            "me.gimmesomepeace.studyhub.subject.component.fixtures.ComponentTestData#invalidUpdateComponentRequests",
        )
        fun `should return 400 when request is invalid`(requestBody: Map<String, Any?>) {
            mvc
                .patch("/subjects/{subjectId}/components/{id}", subjectId, componentId) {
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
        fun `should delete component and return 204`() {
            every { service.delete(subjectId, componentId, userId) } just Runs

            mvc
                .delete("/subjects/{subjectId}/components/{id}", subjectId, componentId) {
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isNoContent() }
                }

            verify { service.delete(subjectId, componentId, userId) }
        }

        @Test
        fun `should return 404 when subject not found`() {
            every { service.delete(subjectId, componentId, userId) } throws SubjectNotFoundException(subjectId)

            mvc
                .delete("/subjects/{subjectId}/components/{id}", subjectId, componentId) {
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isNotFound() }
                    jsonPath("$.code") { value("SUBJECT_NOT_FOUND") }
                }

            verify { service.delete(subjectId, componentId, userId) }
        }
    }
}
