package me.gimmesomepeace.studyhub.subject.api

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import me.gimmesomepeace.studyhub.subject.dto.SubjectListItem
import me.gimmesomepeace.studyhub.subject.exception.NotFoundException
import me.gimmesomepeace.studyhub.subject.exception.SemesterNotFoundException
import me.gimmesomepeace.studyhub.subject.fixtures.semesterId
import me.gimmesomepeace.studyhub.subject.fixtures.subjectCreateRequest
import me.gimmesomepeace.studyhub.subject.fixtures.subjectDetails
import me.gimmesomepeace.studyhub.subject.fixtures.subjectId
import me.gimmesomepeace.studyhub.subject.fixtures.subjectListItem
import me.gimmesomepeace.studyhub.subject.fixtures.subjectUpdateRequest
import me.gimmesomepeace.studyhub.subject.service.SubjectService
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
import org.springframework.web.bind.annotation.ExceptionHandler
import tools.jackson.databind.ObjectMapper
import java.util.UUID

@WebMvcTest(SubjectController::class)
@Import(ExceptionHandler::class)
class SubjectControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var service: SubjectService

    private val subjectId = subjectId()
    private val semesterId = semesterId()

    @Nested
    inner class GetById {
        @Test
        fun `should return subject when found`() {
            val details = subjectDetails(id = subjectId)

            every { service.getById(subjectId) } returns details

            mvc
                .get("/subjects/{id}", subjectId) {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.id") { value(subjectId.toString()) }
                    jsonPath("$.semesterId") { value(semesterId.toString()) }
                    jsonPath("$.name") { value(details.name) }
                    jsonPath("$.code") { value(details.code) }
                    jsonPath("$.teacher") { value(details.teacher) }
                    jsonPath("$.color") { value(details.color) }
                }
        }

        @Test
        fun `should return 404 when subject not found`() {
            every { service.getById(subjectId) } throws NotFoundException(subjectId)

            mvc
                .get("/subjects/{id}", subjectId) {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isNotFound() }
                    jsonPath("$.code") { value("SUBJECT_NOT_FOUND") }
                }
        }
    }

    @Nested
    inner class List {
        @Test
        fun `should return page of subjects`() {
            val items = listOf(
                subjectListItem(name = "Алгоритмы"),
                subjectListItem(name = "Базы данных"),
            )
            val page: Page<SubjectListItem> = PageImpl(items)

            every { service.list(any<Pageable>()) } returns page

            mvc
                .get("/subjects") {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.content") { isArray() }
                    jsonPath("$.content.length()") { value(2) }
                    jsonPath("$.content[0].name") { value("Алгоритмы") }
                    jsonPath("$.content[1].name") { value("Базы данных") }
                    jsonPath("$.totalElements") { value(2) }
                }
        }

        @Test
        fun `should return empty page when no subjects`() {
            val page: Page<SubjectListItem> = PageImpl(emptyList())

            every { service.list(any<Pageable>()) } returns page

            mvc
                .get("/subjects") {
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.content") { isArray() }
                    jsonPath("$.content.length()") { value(0) }
                    jsonPath("$.totalElements") { value(0) }
                }

            verify { service.list(any<Pageable>()) }
        }
    }

    @Nested
    inner class Create {
        @Test
        fun `should create subject and return 201 with location header`() {
            val request = subjectCreateRequest(semesterId = semesterId)

            val created = subjectDetails(
                id = subjectId,
                semesterId = semesterId,
                name = request.name,
                code = request.code,
                teacher = request.teacher,
                color = request.color,
            )

            every { service.create(request) } returns created

            mvc
                .post("/subjects") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isCreated() }
                    header { string("Location", "/subjects/$subjectId") }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.id") { value(subjectId.toString()) }
                    jsonPath("$.semesterId") { value(semesterId.toString()) }
                    jsonPath("$.name") { value(created.name) }
                    jsonPath("$.code") { value(created.code) }
                }

            verify { service.create(request) }
        }

        @Test
        fun `should return 404 when semester not found`() {
            val request = subjectCreateRequest(semesterId = semesterId, name = "Алгоритмы")

            every { service.create(request) } throws SemesterNotFoundException(semesterId)

            mvc
                .post("/subjects") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isNotFound() }
                    jsonPath("$.code") { value("SEMESTER_NOT_FOUND") }
                }
        }

        @ParameterizedTest
        @MethodSource(
            "me.gimmesomepeace.studyhub.subject.fixtures.SubjectTestData#invalidCreateSubjectRequests",
        )
        fun `should return 400 when request is invalid`(requestBody: Map<String, Any?>) {
            mvc
                .post("/subjects") {
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
        fun `should update subject and return 200`() {
            val newName = "Алгоритмы и структуры данных"
            val request = subjectUpdateRequest(name = newName)

            val updated = subjectDetails(
                id = subjectId,
                name = newName,
            )

            every { service.update(subjectId, request) } returns updated

            mvc
                .patch("/subjects/{id}", subjectId) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.id") { value(subjectId.toString()) }
                    jsonPath("$.name") { value(newName) }
                }

            verify { service.update(subjectId, request) }
        }

        @Test
        fun `should return 404 when subject not found`() {
            val request = subjectUpdateRequest(name = "Алгоритмы и структуры данных")

            every { service.update(subjectId, request) } throws NotFoundException(subjectId)

            mvc
                .patch("/subjects/{id}", subjectId) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isNotFound() }
                    jsonPath("$.code") { value("SUBJECT_NOT_FOUND") }
                }

            verify { service.update(subjectId, request) }
        }

        @Test
        fun `should return 404 when new semester not found`() {
            val newSemesterId = UUID.randomUUID()
            val request = subjectUpdateRequest(semesterId = newSemesterId)

            every { service.update(subjectId, request) } throws SemesterNotFoundException(newSemesterId)

            mvc
                .patch("/subjects/{id}", subjectId) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isNotFound() }
                    jsonPath("$.code") { value("SEMESTER_NOT_FOUND") }
                }
        }

        @ParameterizedTest
        @MethodSource(
            "me.gimmesomepeace.studyhub.subject.fixtures.SubjectTestData#invalidUpdateSubjectRequests",
        )
        fun `should return 400 when request is invalid`(requestBody: Map<String, Any?>) {
            mvc
                .patch("/subjects/{id}", subjectId) {
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
        fun `should delete subject and return 204`() {
            every { service.delete(subjectId) } just Runs

            mvc
                .delete("/subjects/{id}", subjectId)
                .andExpect {
                    status { isNoContent() }
                }

            verify { service.delete(subjectId) }
        }
    }
}
