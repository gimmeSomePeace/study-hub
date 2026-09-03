package me.gimmesomepeace.studyhub.subject.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.verify
import me.gimmesomepeace.studyhub.common.id.IdGenerator
import me.gimmesomepeace.studyhub.semester.repository.SemesterRepository
import me.gimmesomepeace.studyhub.subject.entity.SubjectEntity
import me.gimmesomepeace.studyhub.subject.exception.SemesterNotFoundException
import me.gimmesomepeace.studyhub.subject.exception.SubjectNotFoundException
import me.gimmesomepeace.studyhub.subject.fixtures.pageable
import me.gimmesomepeace.studyhub.subject.fixtures.semesterId
import me.gimmesomepeace.studyhub.subject.fixtures.subjectCreateRequest
import me.gimmesomepeace.studyhub.subject.fixtures.subjectEntity
import me.gimmesomepeace.studyhub.subject.fixtures.subjectId
import me.gimmesomepeace.studyhub.subject.fixtures.subjectUpdateRequest
import me.gimmesomepeace.studyhub.subject.fixtures.userId
import me.gimmesomepeace.studyhub.subject.repository.SubjectRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import java.util.UUID

@ExtendWith(MockKExtension::class)
class SubjectServiceTest {
    @MockK
    private lateinit var idGenerator: IdGenerator<UUID>

    @MockK
    private lateinit var semesterRepository: SemesterRepository

    @MockK
    private lateinit var subjectRepository: SubjectRepository

    private lateinit var service: SubjectService

    private val subjectId = subjectId()
    private val semesterId = semesterId()
    private val userId = userId()

    @BeforeEach
    fun setUp() {
        service = SubjectService(idGenerator, semesterRepository, subjectRepository)
    }

    @Nested
    inner class GetById {
        @Test
        fun `should return subject details when found`() {
            val entity = subjectEntity(id = subjectId, semesterId = semesterId)

            every { subjectRepository.findByIdAndOwnerId(subjectId, userId) } returns entity

            val result = service.getById(subjectId, userId)

            assertThat(result.id).isEqualTo(subjectId)
            assertThat(result.semesterId).isEqualTo(semesterId)
            assertThat(result.name).isEqualTo(entity.name)
            assertThat(result.code).isEqualTo(entity.code)
            assertThat(result.teacher).isEqualTo(entity.teacher)
            assertThat(result.color).isEqualTo(entity.color)
            assertThat(result.createdAt).isEqualTo(entity.createdAt)
            assertThat(result.updatedAt).isEqualTo(entity.updatedAt)
        }

        @Test
        fun `should throw NotFoundException when not found`() {
            every { subjectRepository.findByIdAndOwnerId(subjectId, userId) } returns null

            assertThatThrownBy { service.getById(subjectId, userId) }
                .isInstanceOf(SubjectNotFoundException::class.java)
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

            every { subjectRepository.findByOwnerId(userId, pageable) } returns page

            val result = service.list(pageable, userId)

            assertThat(result.content).hasSize(2)
            assertThat(result.content[0].name).isEqualTo("Алгоритмы")
            assertThat(result.content[1].name).isEqualTo("Базы данных")
        }

        @Test
        fun `should return empty page when no subjects`() {
            val page: Page<SubjectEntity> = PageImpl(emptyList<Nothing>())
            val pageable = pageable()

            every { subjectRepository.findByOwnerId(userId, pageable) } returns page

            val result = service.list(pageable, userId)

            assertThat(result.content).isEmpty()
            assertThat(result.totalElements).isEqualTo(0)
        }
    }

    @Nested
    inner class Create {
        @Test
        fun `should create subject and return details`() {
            val request = subjectCreateRequest(semesterId = semesterId)
            val generatedId = subjectId()

            every { idGenerator.generate() } returns generatedId
            every { semesterRepository.existsByIdAndOwnerId(semesterId, userId) } returns true
            every { subjectRepository.save(any()) } answers { firstArg() }

            val result = service.create(request, userId)

            assertThat(result.id).isEqualTo(generatedId)
            assertThat(result.semesterId).isEqualTo(semesterId)
            assertThat(result.name).isEqualTo(request.name)
            assertThat(result.code).isEqualTo(request.code)

            verify { idGenerator.generate() }
            verify { subjectRepository.save(match { it.id == generatedId }) }
        }

        @Test
        fun `should throw SemesterNotFoundException when semester not found`() {
            val request = subjectCreateRequest(semesterId = semesterId)

            val generatedId = subjectId()
            every { idGenerator.generate() } returns generatedId
            every { semesterRepository.existsByIdAndOwnerId(semesterId, userId) } returns false

            assertThatThrownBy { service.create(request, userId) }
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

            every { subjectRepository.findByIdAndOwnerId(subjectId, userId) } returns entity
            every { subjectRepository.save(any()) } answers { firstArg() }

            val result = service.update(subjectId, request, userId)

            assertThat(result.id).isEqualTo(subjectId)
            assertThat(result.name).isEqualTo(newName)

            verify { subjectRepository.save(any()) }
        }

        @Test
        fun `should throw NotFoundException when subject with given id not found`() {
            val request = subjectUpdateRequest(name = "Алгоритмы и структуры данных")

            every { subjectRepository.findByIdAndOwnerId(subjectId, userId) } returns null

            assertThatThrownBy { service.update(subjectId, request, userId) }
                .isInstanceOf(SubjectNotFoundException::class.java)
                .hasMessageContaining(subjectId.toString())
        }

        @Test
        fun `should throw SemesterNotFoundException when new semester not found`() {
            val entity = subjectEntity(
                id = subjectId,
                semesterId = semesterId,
            )

            val newSemesterId = UUID.randomUUID()
            val request = subjectUpdateRequest(semesterId = newSemesterId)

            every { subjectRepository.findByIdAndOwnerId(subjectId, userId) } returns entity
            every { semesterRepository.existsByIdAndOwnerId(newSemesterId, userId) } returns false

            assertThatThrownBy { service.update(subjectId, request, userId) }
                .isInstanceOf(SemesterNotFoundException::class.java)
        }
    }

    @Nested
    inner class Delete {
        @Test
        fun `should call repository deleteById`() {
            every { subjectRepository.deleteByIdAndOwnerId(subjectId, userId) } just Runs

            service.delete(subjectId, userId)

            verify { subjectRepository.deleteByIdAndOwnerId(subjectId, userId) }
        }
    }
}
