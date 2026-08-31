package me.gimmesomepeace.studyhub.deadline.service

import me.gimmesomepeace.studyhub.deadline.dto.DeadlineStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DeadlineStatusTransitionsTest {
    private val transitions = DeadlineStatusTransitions()

    @Nested
    inner class FromOpen {
        @Test
        fun `should allow transition from OPEN to CLOSED`() {
            val result = transitions.canTransitTo(DeadlineStatus.OPEN, DeadlineStatus.CLOSED)

            assertThat(result).isTrue()
        }

        @Test
        fun `should allow transition from OPEN to CANCELLED`() {
            val result = transitions.canTransitTo(DeadlineStatus.OPEN, DeadlineStatus.CANCELLED)

            assertThat(result).isTrue()
        }
    }

    @Nested
    inner class FromClosed {
        @Test
        fun `should allow transition from CLOSED to OPEN`() {
            val result = transitions.canTransitTo(DeadlineStatus.CLOSED, DeadlineStatus.OPEN)

            assertThat(result).isTrue()
        }

        @Test
        fun `should not allow transition from CLOSED to CANCELLED`() {
            val result = transitions.canTransitTo(DeadlineStatus.CLOSED, DeadlineStatus.CANCELLED)

            assertThat(result).isFalse()
        }
    }

    @Nested
    inner class FromCancelled {
        @Test
        fun `should not allow transition from CANCELLED to OPEN`() {
            val result = transitions.canTransitTo(DeadlineStatus.CANCELLED, DeadlineStatus.OPEN)

            assertThat(result).isFalse()
        }

        @Test
        fun `should not allow transition from CANCELLED to CLOSED`() {
            val result = transitions.canTransitTo(DeadlineStatus.CANCELLED, DeadlineStatus.CLOSED)

            assertThat(result).isFalse()
        }
    }
}
