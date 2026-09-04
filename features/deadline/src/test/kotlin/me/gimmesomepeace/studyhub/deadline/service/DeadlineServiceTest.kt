package me.gimmesomepeace.studyhub.deadline.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.verify
import me.gimmesomepeace.studyhub.core.id.IdGenerator
import me.gimmesomepeace.studyhub.deadline.dto.DeadlineStatus
import me.gimmesomepeace.studyhub.deadline.dto.DeadlineType
import me.gimmesomepeace.studyhub.deadline.entity.DeadlineEntity
import me.gimmesomepeace.studyhub.deadline.exception.DeadlineNotFoundException
import me.gimmesomepeace.studyhub.deadline.exception.InvalidStatusTransitionException
import me.gimmesomepeace.studyhub.deadline.fixtures.deadlineComponentId
import me.gimmesomepeace.studyhub.deadline.fixtures.deadlineCreateRequest
import me.gimmesomepeace.studyhub.deadline.fixtures.deadlineEntity
import me.gimmesomepeace.studyhub.deadline.fixtures.deadlineId
import me.gimmesomepeace.studyhub.deadline.fixtures.deadlinePageable
import me.gimmesomepeace.studyhub.deadline.fixtures.deadlineSubjectId
import me.gimmesomepeace.studyhub.deadline.fixtures.deadlineUpdateRequest
import me.gimmesomepeace.studyhub.deadline.fixtures.futureDueAt
import me.gimmesomepeace.studyhub.deadline.fixtures.pastDueAt
import me.gimmesomepeace.studyhub.deadline.fixtures.userId
import me.gimmesomepeace.studyhub.deadline.repository.DeadlineRepository
import me.gimmesomepeace.studyhub.subject.component.exception.ComponentNotFoundException
import me.gimmesomepeace.studyhub.subject.component.repository.ComponentRepository
import me.gimmesomepeace.studyhub.subject.exception.SubjectNotFoundException
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
class DeadlineServiceTest {
    @MockK
    private lateinit var idGenerator: IdGenerator<UUID>

    @MockK
    private lateinit var deadlineRepository: DeadlineRepository

    @MockK
    private lateinit var subjectRepository: SubjectRepository

    @MockK
    private lateinit var componentRepository: ComponentRepository

    @MockK
    private lateinit var statusTransitions: DeadlineStatusTransitions

    private lateinit var service: DeadlineService

    private val deadlineId = deadlineId()
    private val subjectId = deadlineSubjectId()
    private val componentId = deadlineComponentId()
    private val userId = userId()

    @BeforeEach
    fun setUp() {
        service = DeadlineService(
            idGenerator = idGenerator,
            deadlineRepository = deadlineRepository,
            subjectRepository = subjectRepository,
            componentRepository = componentRepository,
            statusTransitions = statusTransitions,
        )
    }

    @Nested
    inner class GetById {
        @Test
        fun `should return deadline details when found`() {
            val entity = deadlineEntity(
                id = deadlineId,
                subjectId = subjectId,
                title = "Лабораторная работа 1",
                type = DeadlineType.LAB,
                status = DeadlineStatus.OPEN,
            )

            every { deadlineRepository.findByIdAndOwnerId(deadlineId, userId) } returns entity

            val result = service.getById(deadlineId, userId)

            assertThat(result.id).isEqualTo(deadlineId)
            assertThat(result.subjectId).isEqualTo(subjectId)
            assertThat(result.title).isEqualTo(entity.title)
            assertThat(result.type).isEqualTo(entity.type)
            assertThat(result.status).isEqualTo(entity.status)
        }

        @Test
        fun `should throw DeadlineNotFoundException when not found`() {
            every { deadlineRepository.findByIdAndOwnerId(deadlineId, userId) } returns null

            assertThatThrownBy { service.getById(deadlineId, userId) }
                .isInstanceOf(DeadlineNotFoundException::class.java)
                .hasMessageContaining(deadlineId.toString())
        }
    }

    @Nested
    inner class List {
        @Test
        fun `should return page of deadline list items`() {
            val entities = listOf(
                deadlineEntity(title = "Лаба 1"),
                deadlineEntity(title = "Лаба 2"),
            )
            val page = PageImpl(entities)
            val pageable = deadlinePageable()

            every { deadlineRepository.findByOwnerId(userId, pageable) } returns page

            val result = service.list(pageable, userId)

            assertThat(result.content).hasSize(2)
            assertThat(result.content[0].title).isEqualTo("Лаба 1")
            assertThat(result.content[1].title).isEqualTo("Лаба 2")
        }

        @Test
        fun `should return empty page when no deadlines`() {
            val page: Page<DeadlineEntity> = PageImpl(emptyList<Nothing>())
            val pageable = deadlinePageable()

            every { deadlineRepository.findByOwnerId(userId, pageable) } returns page

            val result = service.list(pageable, userId)

            assertThat(result.content).isEmpty()
            assertThat(result.totalElements).isEqualTo(0)
        }
    }

    @Nested
    inner class Create {
        @Test
        fun `should create deadline and return details`() {
            val request = deadlineCreateRequest(
                subjectId = subjectId,
                componentId = componentId,
                title = "Лабораторная работа 1",
                type = DeadlineType.LAB,
            )

            val generatedId = deadlineId()

            every { subjectRepository.existsByIdAndOwnerId(subjectId, userId) } returns true
            every { componentRepository.existsByIdAndSubjectId(componentId, subjectId) } returns true
            every { idGenerator.generate() } returns generatedId
            every { deadlineRepository.save(any()) } answers { firstArg() }

            val result = service.create(request, userId)

            assertThat(result.id).isEqualTo(generatedId)
            assertThat(result.subjectId).isEqualTo(subjectId)
            assertThat(result.title).isEqualTo(request.title)
            assertThat(result.type).isEqualTo(request.type)

            verify { subjectRepository.existsByIdAndOwnerId(subjectId, userId) }
            verify { componentRepository.existsByIdAndSubjectId(componentId, subjectId) }
            verify { idGenerator.generate() }
            verify { deadlineRepository.save(any()) }
        }

        @Test
        fun `should throw SubjectNotFoundException when subject not found`() {
            val request = deadlineCreateRequest(subjectId = subjectId)

            every { subjectRepository.existsByIdAndOwnerId(subjectId, userId) } returns false

            assertThatThrownBy { service.create(request, userId) }
                .isInstanceOf(SubjectNotFoundException::class.java)
                .hasMessageContaining(subjectId.toString())

            verify(exactly = 0) { deadlineRepository.save(any()) }
        }

        @Test
        fun `should throw ComponentNotFoundException when component not found`() {
            val request = deadlineCreateRequest(
                subjectId = subjectId,
                componentId = componentId,
            )

            every { subjectRepository.existsByIdAndOwnerId(subjectId, userId) } returns true
            every { componentRepository.existsByIdAndSubjectId(componentId, subjectId) } returns false

            assertThatThrownBy { service.create(request, userId) }
                .isInstanceOf(ComponentNotFoundException::class.java)
                .hasMessageContaining(componentId.toString())

            verify(exactly = 0) { deadlineRepository.save(any()) }
        }

        // TODO: переделать под него
//        @Test
//        fun `should throw ValidationException when component does not belong to subject`() {
//            val otherSubjectId = deadlineSubjectId()
//            val component = deadlineComponent(
//                id = componentId,
//                subjectId = otherSubjectId,
//            )
//
//            val request = deadlineCreateRequest(
//                subjectId = subjectId,
//                componentId = componentId,
//            )
//
//            every { subjectRepository.existsById(subjectId) } returns true
//            every { componentRepository.findById(componentId) } returns Optional.of(component)
//
//            assertThatThrownBy { service.create(request, userId) }
//                .isInstanceOf(ValidationException::class.java)
//                .hasMessageContaining(componentId.toString())
//                .hasMessageContaining(subjectId.toString())
//
//            verify { subjectRepository.existsById(subjectId) }
//            verify { componentRepository.findById(componentId) }
//            verify(exactly = 0) { deadlineRepository.save(any()) }
//        }
    }

    @Nested
    inner class Update {
        @Test
        fun `should update deadline and return details`() {
            val entity = deadlineEntity(
                id = deadlineId,
                subjectId = subjectId,
                title = "Лабораторная работа 1",
                dueAt = pastDueAt(),
                type = DeadlineType.LAB,
                status = DeadlineStatus.OPEN,
                notes = "some info",
            )

            val newSubjectId = deadlineSubjectId()
            val newComponentId = deadlineComponentId()
            val request = deadlineUpdateRequest(
                subjectId = newSubjectId,
                componentId = newComponentId,
                title = "Лабораторная работа 1: Обновлённое название",
                dueAt = futureDueAt(),
                type = DeadlineType.EXAM,
                notes = "some new info",
            )

            every { deadlineRepository.findByIdAndOwnerId(deadlineId, userId) } returns entity
            every { subjectRepository.existsByIdAndOwnerId(newSubjectId, userId) } returns true
            every { componentRepository.existsByIdAndSubjectId(newComponentId, newSubjectId) } returns true
            every { deadlineRepository.save(any()) } answers { firstArg() }

            val result = service.update(deadlineId, request, userId)

            assertThat(result.id).isEqualTo(deadlineId)
            assertThat(result.subjectId).isEqualTo(newSubjectId)
            assertThat(result.componentId).isEqualTo(newComponentId)
            assertThat(result.title).isEqualTo(request.title)
            assertThat(result.dueAt).isEqualTo(request.dueAt)
            assertThat(result.type).isEqualTo(request.type)
            assertThat(result.notes).isEqualTo(request.notes)

            verify { deadlineRepository.save(any()) }
        }

        @Test
        fun `should throw DeadlineNotFoundException when not found`() {
            val request = deadlineUpdateRequest(title = "Лабораторная работа 1: Обновлённое название")

            every { deadlineRepository.findByIdAndOwnerId(deadlineId, userId) } returns null

            assertThatThrownBy { service.update(deadlineId, request, userId) }
                .isInstanceOf(DeadlineNotFoundException::class.java)
                .hasMessageContaining(deadlineId.toString())

            verify(exactly = 0) { deadlineRepository.save(any()) }
        }

        @Test
        fun `should throw SubjectNotFoundException when new subject not found`() {
            val newSubjectId = deadlineSubjectId()
            val entity = deadlineEntity(id = deadlineId, subjectId = subjectId)

            val request = deadlineUpdateRequest(subjectId = newSubjectId)

            every { deadlineRepository.findByIdAndOwnerId(deadlineId, userId) } returns entity
            every { subjectRepository.existsByIdAndOwnerId(newSubjectId, userId) } returns false

            assertThatThrownBy { service.update(deadlineId, request, userId) }
                .isInstanceOf(SubjectNotFoundException::class.java)
                .hasMessageContaining(newSubjectId.toString())

            verify(exactly = 0) { deadlineRepository.save(any()) }
        }

        @Test
        fun `should throw ComponentNotFoundException when new component not found`() {
            val newComponentId = deadlineComponentId()
            val entity = deadlineEntity(id = deadlineId, subjectId = subjectId, componentId = componentId)

            val request = deadlineUpdateRequest(componentId = newComponentId)

            every { deadlineRepository.findByIdAndOwnerId(deadlineId, userId) } returns entity
            every { subjectRepository.existsByIdAndOwnerId(subjectId, userId) } returns true
            every { componentRepository.existsByIdAndSubjectId(newComponentId, subjectId) } returns false

            assertThatThrownBy { service.update(deadlineId, request, userId) }
                .isInstanceOf(ComponentNotFoundException::class.java)
                .hasMessageContaining(newComponentId.toString())

            verify(exactly = 0) { deadlineRepository.save(any()) }
        }
    }

    @Nested
    inner class Delete {
        @Test
        fun `should call repository deleteById`() {
            every { deadlineRepository.deleteByIdAndOwnerId(deadlineId, userId) } just Runs

            service.delete(deadlineId, userId)

            verify { deadlineRepository.deleteByIdAndOwnerId(deadlineId, userId) }
        }
    }

    @Nested
    inner class CloseDeadline {
        @Test
        fun `should close deadline when status is OPEN`() {
            val entity = deadlineEntity(
                id = deadlineId,
                status = DeadlineStatus.OPEN,
            )

            every { deadlineRepository.findByIdAndOwnerId(deadlineId, userId) } returns entity
            every { statusTransitions.canTransitTo(DeadlineStatus.OPEN, DeadlineStatus.CLOSED) } returns true
            every { deadlineRepository.save(any()) } answers { firstArg() }

            val result = service.closeDeadline(deadlineId, userId)

            assertThat(result.status).isEqualTo(DeadlineStatus.CLOSED)

            verify { statusTransitions.canTransitTo(DeadlineStatus.OPEN, DeadlineStatus.CLOSED) }
            verify { deadlineRepository.save(any()) }
        }

        @Test
        fun `should return same deadline when already CLOSED (idempotent)`() {
            val entity = deadlineEntity(
                id = deadlineId,
                status = DeadlineStatus.CLOSED,
            )

            every { deadlineRepository.findByIdAndOwnerId(deadlineId, userId) } returns entity

            val result = service.closeDeadline(deadlineId, userId)

            assertThat(result.status).isEqualTo(DeadlineStatus.CLOSED)

            verify(exactly = 0) { statusTransitions.canTransitTo(any(), any()) }
            verify(exactly = 0) { deadlineRepository.save(any()) }
        }

        @Test
        fun `should throw InvalidStatusTransitionException when transition not allowed`() {
            val entity = deadlineEntity(
                id = deadlineId,
                status = DeadlineStatus.CANCELLED,
            )

            every { deadlineRepository.findByIdAndOwnerId(deadlineId, userId) } returns entity
            every { statusTransitions.canTransitTo(DeadlineStatus.CANCELLED, DeadlineStatus.CLOSED) } returns false

            assertThatThrownBy { service.closeDeadline(deadlineId, userId) }
                .isInstanceOf(InvalidStatusTransitionException::class.java)
                .hasMessageContaining("CANCELLED")
                .hasMessageContaining("CLOSED")

            verify { statusTransitions.canTransitTo(DeadlineStatus.CANCELLED, DeadlineStatus.CLOSED) }
            verify(exactly = 0) { deadlineRepository.save(any()) }
        }

        @Test
        fun `should throw DeadlineNotFoundException when not found`() {
            every { deadlineRepository.findByIdAndOwnerId(deadlineId, userId) } returns null

            assertThatThrownBy { service.closeDeadline(deadlineId, userId) }
                .isInstanceOf(DeadlineNotFoundException::class.java)
                .hasMessageContaining(deadlineId.toString())
        }
    }

    @Nested
    inner class ReopenDeadline {
        @Test
        fun `should reopen deadline when status is CLOSED`() {
            val entity = deadlineEntity(
                id = deadlineId,
                status = DeadlineStatus.CLOSED,
            )

            every { deadlineRepository.findByIdAndOwnerId(deadlineId, userId) } returns entity
            every { statusTransitions.canTransitTo(DeadlineStatus.CLOSED, DeadlineStatus.OPEN) } returns true
            every { deadlineRepository.save(any()) } answers { firstArg() }

            val result = service.reopenDeadline(deadlineId, userId)

            assertThat(result.status).isEqualTo(DeadlineStatus.OPEN)

            verify { statusTransitions.canTransitTo(DeadlineStatus.CLOSED, DeadlineStatus.OPEN) }
            verify { deadlineRepository.save(any()) }
        }

        @Test
        fun `should return same deadline when already OPEN (idempotent)`() {
            val entity = deadlineEntity(
                id = deadlineId,
                status = DeadlineStatus.OPEN,
            )

            every { deadlineRepository.findByIdAndOwnerId(deadlineId, userId) } returns entity

            val result = service.reopenDeadline(deadlineId, userId)

            assertThat(result.status).isEqualTo(DeadlineStatus.OPEN)

            verify(exactly = 0) { deadlineRepository.save(any()) }
        }

        @Test
        fun `should throw InvalidStatusTransitionException when transition not allowed`() {
            val entity = deadlineEntity(
                id = deadlineId,
                status = DeadlineStatus.CANCELLED,
            )

            every { deadlineRepository.findByIdAndOwnerId(deadlineId, userId) } returns entity
            every { statusTransitions.canTransitTo(DeadlineStatus.CANCELLED, DeadlineStatus.OPEN) } returns false

            assertThatThrownBy { service.reopenDeadline(deadlineId, userId) }
                .isInstanceOf(InvalidStatusTransitionException::class.java)
                .hasMessageContaining("CANCELLED")
                .hasMessageContaining("OPEN")

            verify(exactly = 0) { deadlineRepository.save(any()) }
        }
    }

    @Nested
    inner class CancelDeadline {
        @Test
        fun `should cancel deadline when status is OPEN`() {
            val entity = deadlineEntity(
                id = deadlineId,
                status = DeadlineStatus.OPEN,
            )

            every { deadlineRepository.findByIdAndOwnerId(deadlineId, userId) } returns entity
            every { statusTransitions.canTransitTo(DeadlineStatus.OPEN, DeadlineStatus.CANCELLED) } returns true
            every { deadlineRepository.save(any()) } answers { firstArg() }

            val result = service.cancelDeadline(deadlineId, userId)

            assertThat(result.status).isEqualTo(DeadlineStatus.CANCELLED)

            verify { statusTransitions.canTransitTo(DeadlineStatus.OPEN, DeadlineStatus.CANCELLED) }
            verify { deadlineRepository.save(any()) }
        }

        @Test
        fun `should return same deadline when already CANCELLED (idempotent)`() {
            val entity = deadlineEntity(
                id = deadlineId,
                status = DeadlineStatus.CANCELLED,
            )

            every { deadlineRepository.findByIdAndOwnerId(deadlineId, userId) } returns entity

            val result = service.cancelDeadline(deadlineId, userId)

            assertThat(result.status).isEqualTo(DeadlineStatus.CANCELLED)

            verify(exactly = 0) { statusTransitions.canTransitTo(any(), any()) }
            verify(exactly = 0) { deadlineRepository.save(any()) }
        }

        @Test
        fun `should throw InvalidStatusTransitionException when transition not allowed`() {
            val entity = deadlineEntity(
                id = deadlineId,
                status = DeadlineStatus.CLOSED,
            )

            every { deadlineRepository.findByIdAndOwnerId(deadlineId, userId) } returns entity
            every { statusTransitions.canTransitTo(DeadlineStatus.CLOSED, DeadlineStatus.CANCELLED) } returns false

            assertThatThrownBy { service.cancelDeadline(deadlineId, userId) }
                .isInstanceOf(InvalidStatusTransitionException::class.java)
                .hasMessageContaining("CLOSED")
                .hasMessageContaining("CANCELLED")

            verify(exactly = 0) { deadlineRepository.save(any()) }
        }
    }
}
