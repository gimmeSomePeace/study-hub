package me.gimmesomepeace.studyhub.semester.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.verify
import me.gimmesomepeace.studyhub.core.id.IdGenerator
import me.gimmesomepeace.studyhub.semester.exception.SemesterNotFoundException
import me.gimmesomepeace.studyhub.semester.exception.SemesterValidationException
import me.gimmesomepeace.studyhub.semester.fixtures.pageable
import me.gimmesomepeace.studyhub.semester.fixtures.semesterCreateRequest
import me.gimmesomepeace.studyhub.semester.fixtures.semesterEntity
import me.gimmesomepeace.studyhub.semester.fixtures.semesterId
import me.gimmesomepeace.studyhub.semester.fixtures.semesterUpdateRequest
import me.gimmesomepeace.studyhub.semester.fixtures.userId
import me.gimmesomepeace.studyhub.semester.repository.SemesterRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import java.time.LocalDate
import java.util.UUID

@ExtendWith(MockKExtension::class)
class SemesterServiceTest {
    @MockK
    private lateinit var idGenerator: IdGenerator<UUID>

    @MockK
    private lateinit var semesterRepository: SemesterRepository

    private lateinit var service: SemesterService

    private val semesterId = semesterId()
    private val userId = userId()

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
                ownerId = userId,
                name = "Осень 2026",
                startsAt = LocalDate.of(2026, 9, 1),
                endsAt = LocalDate.of(2026, 12, 28),
            )

            every { semesterRepository.findByIdAndOwnerId(semesterId, userId) } returns entity

            val result = service.getById(semesterId, userId)

            assertThat(result.id).isEqualTo(semesterId)
            assertThat(result.name).isEqualTo("Осень 2026")
            assertThat(result.startsAt).isEqualTo(entity.startsAt)
            assertThat(result.endsAt).isEqualTo(entity.endsAt)
        }

        @Test
        fun `should throw SemesterNotFoundException when not found`() {
            every { semesterRepository.findByIdAndOwnerId(semesterId, userId) } returns null

            assertThatThrownBy { service.getById(semesterId, userId) }
                .isInstanceOf(SemesterNotFoundException::class.java)
                .hasMessageContaining(semesterId.toString())
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

            every { semesterRepository.findByOwnerId(userId, pageable) } returns page

            val result = service.list(pageable, userId)

            assertThat(result.content).hasSize(2)
            assertThat(result.content[0].name).isEqualTo("Осень 2026")
            assertThat(result.content[1].name).isEqualTo("Весна 2027")
            assertThat(result.totalElements).isEqualTo(2)
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

            val result = service.create(request, userId)

            assertThat(result.id).isEqualTo(generatedId)
            assertThat(result.name).isEqualTo("Осень 2026")
            assertThat(result.startsAt).isEqualTo(LocalDate.of(2026, 9, 1))
            assertThat(result.endsAt).isEqualTo(LocalDate.of(2026, 12, 28))

            verify { idGenerator.generate() }
            verify { semesterRepository.save(match { semester -> semester.ownerId == userId }) }
        }

        @Test
        fun `should throw exception when endsAt is before startsAt`() {
            val request = semesterCreateRequest(
                startsAt = LocalDate.of(2026, 12, 28),
                endsAt = LocalDate.of(2026, 9, 1),
            )

            assertThatThrownBy { service.create(request, userId) }
                .isInstanceOf(SemesterValidationException::class.java)

            verify(exactly = 0) { semesterRepository.save(any()) }
        }

        @Test
        fun `should throw exception when endsAt equals startsAt`() {
            val request = semesterCreateRequest(
                startsAt = LocalDate.of(2026, 9, 1),
                endsAt = LocalDate.of(2026, 9, 1),
            )

            assertThatThrownBy { service.create(request, userId) }
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
                ownerId = userId,
                name = "Осень 2026",
                startsAt = LocalDate.of(2026, 9, 1),
                endsAt = LocalDate.of(2026, 12, 28),
            )

            val request = semesterUpdateRequest(name = "Осень 2026/2027")

            every { semesterRepository.findByIdAndOwnerId(semesterId, userId) } returns entity
            every { semesterRepository.save(any()) } answers { firstArg() }

            val result = service.update(semesterId, request, userId)

            assertThat(result.id).isEqualTo(semesterId)
            assertThat(result.name).isEqualTo(request.name)
            assertThat(result.startsAt).isEqualTo(entity.startsAt)
            assertThat(result.endsAt).isEqualTo(entity.endsAt)

            verify { semesterRepository.save(any()) }
        }

        @Test
        fun `should throw SemesterNotFoundException when not found`() {
            val request = semesterUpdateRequest(name = "Осень 2026/2027")

            every { semesterRepository.findByIdAndOwnerId(semesterId, userId) } returns null

            assertThatThrownBy { service.update(semesterId, request, userId) }
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

            every { semesterRepository.findByIdAndOwnerId(semesterId, userId) } returns entity

            assertThatThrownBy { service.update(semesterId, request, userId) }
                .isInstanceOf(SemesterValidationException::class.java)

            verify(exactly = 0) { semesterRepository.save(any()) }
        }
    }

    @Nested
    inner class Delete {
        @Test
        fun `should call repository deleteById`() {
            every { semesterRepository.deleteByIdAndOwnerId(semesterId, userId) } just Runs

            service.delete(semesterId, userId)

            verify { semesterRepository.deleteByIdAndOwnerId(semesterId, userId) }
        }
    }
}
