package me.gimmesomepeace.studyhub.subject.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.verify
import me.gimmesomepeace.studyhub.common.IdGenerator
import me.gimmesomepeace.studyhub.subject.entity.SubjectEntity
import me.gimmesomepeace.studyhub.subject.exception.NotFoundException
import me.gimmesomepeace.studyhub.subject.exception.SemesterNotFoundException
import me.gimmesomepeace.studyhub.subject.fixtures.pageable
import me.gimmesomepeace.studyhub.subject.fixtures.semesterId
import me.gimmesomepeace.studyhub.subject.fixtures.subjectCreateRequest
import me.gimmesomepeace.studyhub.subject.fixtures.subjectEntity
import me.gimmesomepeace.studyhub.subject.fixtures.subjectId
import me.gimmesomepeace.studyhub.subject.fixtures.subjectUpdateRequest
import me.gimmesomepeace.studyhub.subject.repository.SubjectRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import java.sql.SQLException
import java.util.Optional
import java.util.UUID
import kotlin.jvm.java

@ExtendWith(MockKExtension::class)
class SubjectServiceTest {
    @MockK
    private lateinit var idGenerator: IdGenerator<UUID>

    @MockK
    private lateinit var subjectRepository: SubjectRepository

    private lateinit var service: SubjectService

    private val subjectId = subjectId()
    private val semesterId = semesterId()

    @BeforeEach
    fun setUp() {
        service = SubjectService(idGenerator, subjectRepository)
    }

    @Nested
    inner class GetById {
        @Test
        fun `should return subject details when found`() {
            val entity = subjectEntity(id = subjectId, semesterId = semesterId)

            every { subjectRepository.findById(subjectId) } returns Optional.of(entity)

            val result = service.getById(subjectId)

            assertThat(result.id).isEqualTo(subjectId)
            assertThat(result.semesterId).isEqualTo(semesterId)
            assertThat(result.name).isEqualTo(entity.name)
            assertThat(result.code).isEqualTo(entity.code)
        }

        @Test
        fun `should throw NotFoundException when not found`() {
            every { subjectRepository.findById(subjectId) } returns Optional.empty()

            assertThatThrownBy { service.getById(subjectId) }
                .isInstanceOf(NotFoundException::class.java)
                .hasMessageContaining(subjectId.toString())
        }
    }

    @Nested
    inner class List {
        @Test
        fun `should return page of subject list items`() {
            val entities = listOf(
                subjectEntity(name = "Алгоритмы"),
                subjectEntity(name = "Базы данных"),
            )
            val page = PageImpl(entities)
            val pageable = pageable()

            every { subjectRepository.findAll(pageable) } returns page

            val result = service.list(pageable)

            assertThat(result.content).hasSize(2)
            assertThat(result.content[0].name).isEqualTo("Алгоритмы")
            assertThat(result.content[1].name).isEqualTo("Базы данных")
        }

        @Test
        fun `should return empty page when no subjects`() {
            val page: Page<SubjectEntity> = PageImpl(emptyList<Nothing>())
            val pageable = pageable()

            every { subjectRepository.findAll(pageable) } returns page

            val result = service.list(pageable)

            assertThat(result.content).isEmpty()
            assertThat(result.totalElements).isEqualTo(0)

            verify { subjectRepository.findAll(pageable) }
        }
    }

    @Nested
    inner class Create {
        @Test
        fun `should create subject and return details`() {
            val request = subjectCreateRequest(semesterId = semesterId)
            val generatedId = subjectId()

            every { idGenerator.generate() } returns generatedId
            every { subjectRepository.saveAndFlush(any()) } answers { firstArg() }

            val result = service.create(request)

            assertThat(result.id).isEqualTo(generatedId)
            assertThat(result.semesterId).isEqualTo(semesterId)
            assertThat(result.name).isEqualTo(request.name)
            assertThat(result.code).isEqualTo(request.code)

            verify { idGenerator.generate() }
            verify { subjectRepository.saveAndFlush(any()) }
        }

        @Test
        fun `should throw SemesterNotFoundException when semester not found`() {
            val request = subjectCreateRequest(semesterId = semesterId)

            val generatedId = subjectId()
            every { idGenerator.generate() } returns generatedId

            val exception = DataIntegrityViolationException(
                "Could not execute statement",
                SQLException(
                    "ERROR: insert or update on table \"subject\" violates foreign key constraint \"fk_subject_semester\"",
                ),
            )
            every { subjectRepository.saveAndFlush(any()) } throws exception

            assertThatThrownBy { service.create(request) }
                .isInstanceOf(SemesterNotFoundException::class.java)
                .hasMessageContaining(semesterId.toString())
        }
    }

    @Nested
    inner class Update {
        @Test
        fun `should update subject and return details`() {
            val newName = "Алгоритмы и структуры данных"
            val entity = subjectEntity(
                id = subjectId,
                name = "Алгоритмы",
            )

            val request = subjectUpdateRequest(name = newName)

            every { subjectRepository.findById(subjectId) } returns Optional.of(entity)
            every { subjectRepository.saveAndFlush(any()) } answers { firstArg() }

            val result = service.update(subjectId, request)

            assertThat(result.id).isEqualTo(subjectId)
            assertThat(result.name).isEqualTo(newName)

            verify { subjectRepository.saveAndFlush(any()) }
        }

        @Test
        fun `should throw NotFoundException when subject with given id not found`() {
            val request = subjectUpdateRequest(name = "Алгоритмы и структуры данных")

            every { subjectRepository.findById(subjectId) } returns Optional.empty()

            assertThatThrownBy { service.update(subjectId, request) }
                .isInstanceOf(NotFoundException::class.java)
                .hasMessageContaining(subjectId.toString())
        }

        @Test
        fun `should update only provided fields`() {
            val entity = subjectEntity(
                id = subjectId,
                semesterId = semesterId,
                name = "Алгоритмы",
                code = "CS101",
                teacher = "Иванов И.И.",
            )

            val request = subjectUpdateRequest(name = "Алгоритмы и структуры данных")

            every { subjectRepository.findById(subjectId) } returns Optional.of(entity)
            every { subjectRepository.saveAndFlush(any()) } answers { firstArg() }

            val result = service.update(subjectId, request)

            assertThat(result.name).isEqualTo("Алгоритмы и структуры данных")
            assertThat(result.code).isEqualTo("CS101")
            assertThat(result.teacher).isEqualTo("Иванов И.И.")
        }

        @Test
        fun `should throw SemesterNotFoundException when new semester not found`() {
            val entity = subjectEntity(
                id = subjectId,
                semesterId = semesterId,
            )

            val newSemesterId = UUID.randomUUID()
            val request = subjectUpdateRequest(semesterId = newSemesterId)

            every { subjectRepository.findById(subjectId) } returns Optional.of(entity)

            val exception = DataIntegrityViolationException(
                "Could not execute statement",
                SQLException(
                    "ERROR: insert or update on table \"subject\" violates foreign key constraint \"fk_subject_semester\"",
                ),
            )
            every { subjectRepository.saveAndFlush(any()) } throws exception

            assertThatThrownBy { service.update(subjectId, request) }
                .isInstanceOf(SemesterNotFoundException::class.java)
        }
    }

    @Nested
    inner class Delete {
        @Test
        fun `should call repository deleteById`() {
            every { subjectRepository.deleteById(subjectId) } just Runs

            service.delete(subjectId)

            verify { subjectRepository.deleteById(subjectId) }
        }

        @Test
        fun `should not throw exception when subject not found (idempotent)`() {
            every { subjectRepository.deleteById(subjectId) } just Runs

            assertThatCode { service.delete(subjectId) }
                .doesNotThrowAnyException()

            verify { subjectRepository.deleteById(subjectId) }
        }
    }
}
