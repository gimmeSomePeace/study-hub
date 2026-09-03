package me.gimmesomepeace.studyhub.subject.component.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.verify
import me.gimmesomepeace.studyhub.common.id.IdGenerator
import me.gimmesomepeace.studyhub.subject.component.dto.ComponentType
import me.gimmesomepeace.studyhub.subject.component.entity.ComponentEntity
import me.gimmesomepeace.studyhub.subject.component.exception.ComponentNotFoundException
import me.gimmesomepeace.studyhub.subject.component.fixtures.componentCreateRequest
import me.gimmesomepeace.studyhub.subject.component.fixtures.componentEntity
import me.gimmesomepeace.studyhub.subject.component.fixtures.componentId
import me.gimmesomepeace.studyhub.subject.component.fixtures.componentPageable
import me.gimmesomepeace.studyhub.subject.component.fixtures.componentUpdateRequest
import me.gimmesomepeace.studyhub.subject.component.repository.ComponentRepository
import me.gimmesomepeace.studyhub.subject.exception.SubjectNotFoundException
import me.gimmesomepeace.studyhub.subject.fixtures.subjectId
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
class ComponentServiceTest {
    @MockK
    private lateinit var idGenerator: IdGenerator<UUID>

    @MockK
    private lateinit var componentRepository: ComponentRepository

    @MockK
    private lateinit var subjectRepository: SubjectRepository

    private lateinit var service: ComponentService

    private val subjectId = subjectId()
    private val componentId = componentId()
    private val userId = userId()

    @BeforeEach
    fun setUp() {
        service = ComponentService(idGenerator, componentRepository, subjectRepository)
    }

    @Nested
    inner class GetById {
        @Test
        fun `should return component details when found`() {
            val entity = componentEntity(id = componentId, subjectId = subjectId)

            every { subjectRepository.existsByIdAndOwnerId(subjectId, userId) } returns true
            every { componentRepository.findByIdAndSubjectId(componentId, subjectId) } returns entity

            val result = service.getById(subjectId, componentId, userId)

            assertThat(result.id).isEqualTo(componentId)
            assertThat(result.subjectId).isEqualTo(subjectId)
            assertThat(result.type).isEqualTo(entity.type)
            assertThat(result.title).isEqualTo(entity.title)
            assertThat(result.priority).isEqualTo(entity.priority)
            assertThat(result.notes).isEqualTo(entity.notes)

            verify { subjectRepository.existsByIdAndOwnerId(subjectId, userId) }
        }

        @Test
        fun `should throw SubjectNotFoundException when subject not found`() {
            every { subjectRepository.existsByIdAndOwnerId(subjectId, userId) } returns false

            assertThatThrownBy { service.getById(subjectId, componentId, userId) }
                .isInstanceOf(SubjectNotFoundException::class.java)
                .hasMessageContaining(subjectId.toString())
        }

        @Test
        fun `should throw ComponentNotFoundException when component not found`() {
            every { subjectRepository.existsByIdAndOwnerId(subjectId, userId) } returns true
            every { componentRepository.findByIdAndSubjectId(componentId, subjectId) } returns null

            assertThatThrownBy { service.getById(subjectId, componentId, userId) }
                .isInstanceOf(ComponentNotFoundException::class.java)
                .hasMessageContaining(componentId.toString())
        }
    }

    @Nested
    inner class List {
        @Test
        fun `should return page of component list items`() {
            val entities = listOf(
                componentEntity(subjectId = subjectId, title = "Лекция 1"),
                componentEntity(subjectId = subjectId, title = "Лаба 1"),
            )
            val page = PageImpl(entities)
            val pageable = componentPageable()

            every { subjectRepository.existsByIdAndOwnerId(subjectId, userId) } returns true
            every { componentRepository.findBySubjectId(subjectId, pageable) } returns page

            val result = service.list(subjectId, pageable, userId)

            assertThat(result.content).hasSize(2)
            assertThat(result.content[0].title).isEqualTo("Лекция 1")
            assertThat(result.content[1].title).isEqualTo("Лаба 1")
        }

        @Test
        fun `should return empty page when no components`() {
            val page: Page<ComponentEntity> = PageImpl(emptyList<Nothing>())
            val pageable = componentPageable()

            every { subjectRepository.existsByIdAndOwnerId(subjectId, userId) } returns true
            every { componentRepository.findBySubjectId(subjectId, pageable) } returns page

            val result = service.list(subjectId, pageable, userId)

            assertThat(result.content).isEmpty()
            assertThat(result.totalElements).isEqualTo(0)
        }

        @Test
        fun `should throw SubjectNotFoundException when subject not found`() {
            val pageable = componentPageable()

            every { subjectRepository.existsByIdAndOwnerId(subjectId, userId) } returns false

            assertThatThrownBy { service.list(subjectId, pageable, userId) }
                .isInstanceOf(SubjectNotFoundException::class.java)
                .hasMessageContaining(subjectId.toString())
        }
    }

    @Nested
    inner class Create {
        @Test
        fun `should create component and return details`() {
            val request = componentCreateRequest(
                type = ComponentType.LECTURE,
                title = "Лекция 1: Введение",
                priority = 5,
                notes = "Базовые понятия",
            )

            val generatedId = componentId()

            every { subjectRepository.existsByIdAndOwnerId(subjectId, userId) } returns true
            every { idGenerator.generate() } returns generatedId
            every { componentRepository.save(any()) } answers { firstArg() }

            val result = service.create(subjectId, request, userId)

            assertThat(result.id).isEqualTo(generatedId)
            assertThat(result.subjectId).isEqualTo(subjectId)

            verify { componentRepository.save(match { it.subjectId == subjectId }) }
        }

        @Test
        fun `should throw SubjectNotFoundException when subject not found`() {
            val request = componentCreateRequest(title = "Лекция 1: Введение")

            every { subjectRepository.existsByIdAndOwnerId(subjectId, userId) } returns false

            assertThatThrownBy { service.create(subjectId, request, userId) }
                .isInstanceOf(SubjectNotFoundException::class.java)
                .hasMessageContaining(subjectId.toString())
        }
    }

    @Nested
    inner class Update {
        @Test
        fun `should update component and return details`() {
            val entity = componentEntity(
                id = componentId,
                subjectId = subjectId,
                type = ComponentType.LECTURE,
                title = "Лекция 1: Введение",
                priority = 3,
                notes = "Старые заметки",
            )

            val request = componentUpdateRequest(
                type = ComponentType.PRACTICE,
                title = "Практика 1: Основы",
                priority = 5,
                notes = "Новые заметки",
            )

            every { subjectRepository.existsByIdAndOwnerId(subjectId, userId) } returns true
            every { componentRepository.findByIdAndSubjectId(componentId, subjectId) } returns entity
            every { componentRepository.save(any()) } answers { firstArg() }

            val result = service.update(subjectId, componentId, request, userId)

            assertThat(result.type).isEqualTo(request.type)
            assertThat(result.title).isEqualTo(request.title)
            assertThat(result.priority).isEqualTo(request.priority)
            assertThat(result.notes).isEqualTo(request.notes)
        }

        @Test
        fun `should throw SubjectNotFoundException when subject not found`() {
            val request = componentUpdateRequest(title = "Лекция 1: Обновлённое название")

            every { subjectRepository.existsByIdAndOwnerId(subjectId, userId) } returns false

            assertThatThrownBy { service.update(subjectId, componentId, request, userId) }
                .isInstanceOf(SubjectNotFoundException::class.java)
                .hasMessageContaining(subjectId.toString())
        }

        @Test
        fun `should throw ComponentNotFoundException when component not found`() {
            val request = componentUpdateRequest(title = "Лекция 1: Обновлённое название")

            every { subjectRepository.existsByIdAndOwnerId(subjectId, userId) } returns true
            every { componentRepository.findByIdAndSubjectId(componentId, subjectId) } returns null

            assertThatThrownBy { service.update(subjectId, componentId, request, userId) }
                .isInstanceOf(ComponentNotFoundException::class.java)
                .hasMessageContaining(componentId.toString())
        }
    }

    @Nested
    inner class Delete {
        @Test
        fun `should delete component when found`() {
            every { subjectRepository.existsByIdAndOwnerId(subjectId, userId) } returns true
            every { componentRepository.deleteByIdAndSubjectId(subjectId, componentId) } just Runs

            service.delete(subjectId, componentId, userId)

            verify { componentRepository.deleteByIdAndSubjectId(subjectId, componentId) }
        }

        @Test
        fun `should throw SubjectNotFoundException when subject not found`() {
            every { subjectRepository.existsByIdAndOwnerId(subjectId, userId) } returns false

            assertThatThrownBy { service.delete(subjectId, componentId, userId) }
                .isInstanceOf(SubjectNotFoundException::class.java)
                .hasMessageContaining(subjectId.toString())

            verify(exactly = 0) { componentRepository.deleteByIdAndSubjectId(any(), any()) }
        }
    }
}
