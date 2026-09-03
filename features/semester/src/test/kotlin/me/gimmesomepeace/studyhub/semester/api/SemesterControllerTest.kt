package me.gimmesomepeace.studyhub.semester.api

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import me.gimmesomepeace.studyhub.semester.api.create.SemesterCreateRequest
import me.gimmesomepeace.studyhub.semester.api.update.SemesterUpdateRequest
import me.gimmesomepeace.studyhub.semester.dto.SemesterListItem
import me.gimmesomepeace.studyhub.semester.exception.SemesterNotFoundException
import me.gimmesomepeace.studyhub.semester.fixtures.authenticateAs
import me.gimmesomepeace.studyhub.semester.fixtures.semesterDetails
import me.gimmesomepeace.studyhub.semester.fixtures.semesterId
import me.gimmesomepeace.studyhub.semester.fixtures.semesterListItem
import me.gimmesomepeace.studyhub.semester.fixtures.userId
import me.gimmesomepeace.studyhub.semester.service.SemesterService
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
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
import java.time.LocalDate

@WebMvcTest(SemesterController::class)
class SemesterControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var service: SemesterService

    private val semesterId = semesterId()
    private val userId = userId()

    @Nested
    inner class GetById {
        @Test
        fun `should return semester when found`() {
            val details = semesterDetails(id = semesterId)

            every { service.getById(semesterId, userId) } returns details

            mvc
                .get("/semesters/{id}", semesterId) {
                    accept = MediaType.APPLICATION_JSON
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.id") { value(semesterId.toString()) }
                    jsonPath("$.name") { value(details.name) }
                    jsonPath("$.startsAt") { value(details.startsAt.toString()) }
                    jsonPath("$.endsAt") { value(details.endsAt.toString()) }
                    jsonPath("$.createdAt") { value(details.createdAt.toString()) }
                    jsonPath("$.updatedAt") { value(details.updatedAt.toString()) }
                }
        }

        @Test
        fun `should return 404 when semester not found`() {
            every { service.getById(semesterId, userId) } throws SemesterNotFoundException(semesterId)

            mvc
                .get("/semesters/{id}", semesterId) {
                    accept = MediaType.APPLICATION_JSON
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isNotFound() }
                    jsonPath("$.code") { value("SEMESTER_NOT_FOUND") }
                }
        }
    }

    @Nested
    inner class List {
        @Test
        fun `should return page of semesters`() {
            val items = listOf(
                semesterListItem(name = "Осень 2026"),
                semesterListItem(name = "Весна 2027"),
            )
            val page: Page<SemesterListItem> = PageImpl(items)

            every { service.list(any<Pageable>(), userId) } returns page

            mvc
                .get("/semesters") {
                    accept = MediaType.APPLICATION_JSON
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.content") { isArray() }
                    jsonPath("$.content.length()") { value(2) }
                    jsonPath("$.content[0].name") { value("Осень 2026") }
                    jsonPath("$.content[1].name") { value("Весна 2027") }
                    jsonPath("$.totalElements") { value(2) }
                }
        }

        @Test
        fun `should return empty page when no semesters`() {
            val page: Page<SemesterListItem> = PageImpl(emptyList())

            every { service.list(any<Pageable>(), userId) } returns page

            mvc
                .get("/semesters") {
                    accept = MediaType.APPLICATION_JSON
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.content") { isArray() }
                    jsonPath("$.content.length()") { value(0) }
                    jsonPath("$.totalElements") { value(0) }
                }
        }
    }

    @Nested
    inner class Create {
        @Test
        fun `should create semester and return 201 with location header`() {
            val request = SemesterCreateRequest(
                name = "Осень 2026",
                startsAt = LocalDate.of(2026, 9, 1),
                endsAt = LocalDate.of(2026, 12, 28),
            )

            val created = semesterDetails(
                id = semesterId,
                name = request.name,
                startsAt = request.startsAt,
                endsAt = request.endsAt,
            )

            every { service.create(request, userId) } returns created

            mvc
                .post("/semesters") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isCreated() }
                    header { string("Location", "/semesters/$semesterId") }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.id") { value(semesterId.toString()) }
                    jsonPath("$.name") { value(request.name) }
                    jsonPath("$.startsAt") { value(request.startsAt.toString()) }
                    jsonPath("$.endsAt") { value(request.endsAt.toString()) }
                }
        }

        @ParameterizedTest
        @MethodSource(
            "me.gimmesomepeace.studyhub.semester.fixtures.SemesterTestData#invalidCreateSemesterRequests",
        )
        fun `should return 400 when request is invalid`(requestBody: Map<String, Any?>) {
            mvc
                .post("/semesters") {
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
        fun `should update semester and return 200`() {
            val request = SemesterUpdateRequest(name = "Осень 2026/2027")

            val updated = semesterDetails(
                id = semesterId,
                name = "Осень 2026/2027",
            )

            every { service.update(semesterId, request, userId) } returns updated

            mvc
                .patch("/semesters/{id}", semesterId) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.id") { value(semesterId.toString()) }
                    jsonPath("$.name") { value("Осень 2026/2027") }
                }
        }

        @Test
        fun `should return 404 when semester not found`() {
            val request = SemesterUpdateRequest(name = "Осень 2026/2027")

            every { service.update(semesterId, request, userId) } throws SemesterNotFoundException(semesterId)

            mvc
                .patch("/semesters/{id}", semesterId) {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isNotFound() }
                    jsonPath("$.code") { value("SEMESTER_NOT_FOUND") }
                }
        }

        @ParameterizedTest
        @MethodSource(
            "me.gimmesomepeace.studyhub.semester.fixtures.SemesterTestData#invalidUpdateSemesterRequests",
        )
        fun `should return 400 when request is invalid`(requestBody: Map<String, Any?>) {
            mvc
                .patch("/semesters/{id}", semesterId) {
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
        fun `should delete semester and return 204`() {
            every { service.delete(semesterId, userId) } just Runs

            mvc
                .delete("/semesters/{id}", semesterId) {
                    with(authenticateAs(userId))
                }.andExpect {
                    status { isNoContent() }
                }

            verify { service.delete(semesterId, userId) }
        }
    }
}
