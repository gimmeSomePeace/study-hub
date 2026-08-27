package me.gimmesomepeace.studyhub.semester.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.verify
import me.gimmesomepeace.studyhub.common.IdGenerator
import me.gimmesomepeace.studyhub.semester.api.pageable
import me.gimmesomepeace.studyhub.semester.api.semesterCreateRequest
import me.gimmesomepeace.studyhub.semester.api.semesterUpdateRequest
import me.gimmesomepeace.studyhub.semester.dto.semesterId
import me.gimmesomepeace.studyhub.semester.entity.semesterEntity
import me.gimmesomepeace.studyhub.semester.exception.SemesterNotFoundException
import me.gimmesomepeace.studyhub.semester.exception.SemesterValidationException
import me.gimmesomepeace.studyhub.semester.repository.SemesterRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import java.time.LocalDate
import java.util.Optional
import java.util.UUID

@ExtendWith(MockKExtension::class)
class SemesterServiceTest {
    @MockK
    private lateinit var idGenerator: IdGenerator<UUID>

    @MockK
    private lateinit var semesterRepository: SemesterRepository

    private lateinit var service: SemesterService

    private val semesterId = semesterId()

    @BeforeEach
    fun setUp() {
        service = SemesterService(idGenerator, semesterRepository)
    }

    @Nested
    inner class GetById {
        @Test
        fun `should return semester details when found`() {
            val entity = semesterEntity(
                id = semesterId,
                name = "Осень 2026",
                startsAt = LocalDate.of(2026, 9, 1),
                endsAt = LocalDate.of(2026, 12, 28),
            )

            every { semesterRepository.findById(semesterId) } returns Optional.of(entity)

            val result = service.getById(semesterId)

            assertThat(result.id).isEqualTo(semesterId)
            assertThat(result.name).isEqualTo("Осень 2026")
            assertThat(result.startsAt).isEqualTo(LocalDate.of(2026, 9, 1))
            assertThat(result.endsAt).isEqualTo(LocalDate.of(2026, 12, 28))

            verify { semesterRepository.findById(semesterId) }
        }

        @Test
        fun `should throw SemesterNotFoundException when not found`() {
            every { semesterRepository.findById(semesterId) } returns Optional.empty()

            assertThatThrownBy { service.getById(semesterId) }
                .isInstanceOf(SemesterNotFoundException::class.java)
                .hasMessageContaining(semesterId.toString())

            verify { semesterRepository.findById(semesterId) }
        }
    }

    @Nested
    inner class List {
        @Test
        fun `should return page of semester list items`() {
            val entities = listOf(
                semesterEntity(name = "Осень 2026"),
                semesterEntity(name = "Весна 2027"),
            )
            val page = PageImpl(entities)
            val pageable = pageable()

            every { semesterRepository.findAll(pageable) } returns page

            val result = service.list(pageable)

            assertThat(result.content).hasSize(2)
            assertThat(result.content[0].name).isEqualTo("Осень 2026")
            assertThat(result.content[1].name).isEqualTo("Весна 2027")
            assertThat(result.totalElements).isEqualTo(2)

            verify { semesterRepository.findAll(pageable) }
        }
    }

    @Nested
    inner class Create {
        @Test
        fun `should create semester and return details`() {
            val request = semesterCreateRequest(
                name = "Осень 2026",
                startsAt = LocalDate.of(2026, 9, 1),
                endsAt = LocalDate.of(2026, 12, 28),
            )

            val generatedId = semesterId()

            every { idGenerator.generate() } returns generatedId
            every { semesterRepository.save(any()) } answers { firstArg() }

            val result = service.create(request)

            assertThat(result.id).isEqualTo(generatedId)
            assertThat(result.name).isEqualTo("Осень 2026")
            assertThat(result.startsAt).isEqualTo(LocalDate.of(2026, 9, 1))
            assertThat(result.endsAt).isEqualTo(LocalDate.of(2026, 12, 28))

            verify { idGenerator.generate() }
            verify { semesterRepository.save(any()) }
        }

        @Test
        fun `should throw exception when endsAt is before startsAt`() {
            val request = semesterCreateRequest(
                startsAt = LocalDate.of(2026, 12, 28),
                endsAt = LocalDate.of(2026, 9, 1),
            )

            assertThatThrownBy { service.create(request) }
                .isInstanceOf(SemesterValidationException::class.java)

            verify(exactly = 0) { semesterRepository.save(any()) }
        }

        @Test
        fun `should throw exception when endsAt equals startsAt`() {
            val request = semesterCreateRequest(
                startsAt = LocalDate.of(2026, 9, 1),
                endsAt = LocalDate.of(2026, 9, 1),
            )

            assertThatThrownBy { service.create(request) }
                .isInstanceOf(SemesterValidationException::class.java)

            verify(exactly = 0) { semesterRepository.save(any()) }
        }
    }

    @Nested
    inner class Update {
        @Test
        fun `should update semester and return details`() {
            val entity = semesterEntity(
                id = semesterId,
                name = "Осень 2026",
                startsAt = LocalDate.of(2026, 9, 1),
                endsAt = LocalDate.of(2026, 12, 28),
            )

            val request = semesterUpdateRequest(name = "Осень 2026/2027")

            every { semesterRepository.findById(semesterId) } returns Optional.of(entity)
            every { semesterRepository.save(any()) } answers { firstArg() }

            val result = service.update(semesterId, request)

            assertThat(result.id).isEqualTo(semesterId)
            assertThat(result.name).isEqualTo("Осень 2026/2027")
            assertThat(result.startsAt).isEqualTo(LocalDate.of(2026, 9, 1))
            assertThat(result.endsAt).isEqualTo(LocalDate.of(2026, 12, 28))

            verify { semesterRepository.save(any()) }
        }

        @Test
        fun `should throw SemesterNotFoundException when not found`() {
            val request = semesterUpdateRequest(name = "Осень 2026/2027")

            every { semesterRepository.findById(semesterId) } returns Optional.empty()

            assertThatThrownBy { service.update(semesterId, request) }
                .isInstanceOf(SemesterNotFoundException::class.java)

            verify(exactly = 0) { semesterRepository.save(any()) }
        }

        @Test
        fun `should throw SemesterValidationException when dates are invalid after update`() {
            val entity = semesterEntity(
                id = semesterId,
                startsAt = LocalDate.of(2026, 9, 1),
                endsAt = LocalDate.of(2026, 12, 28),
            )

            val request = semesterUpdateRequest(
                endsAt = LocalDate.of(2026, 8, 1),
            )

            every { semesterRepository.findById(semesterId) } returns Optional.of(entity)

            assertThatThrownBy { service.update(semesterId, request) }
                .isInstanceOf(SemesterValidationException::class.java)

            verify(exactly = 0) { semesterRepository.save(any()) }
        }
    }

    @Nested
    inner class Delete {
        @Test
        fun `should call repository deleteById`() {
            every { semesterRepository.deleteById(semesterId) } just Runs

            service.delete(semesterId)

            verify { semesterRepository.deleteById(semesterId) }
        }

        @Test
        fun `should not throw exception when semester not found (idempotent)`() {
            every { semesterRepository.deleteById(semesterId) } just Runs

            assertThatCode { service.delete(semesterId) }
                .doesNotThrowAnyException()

            verify { semesterRepository.deleteById(semesterId) }
        }
    }
}
