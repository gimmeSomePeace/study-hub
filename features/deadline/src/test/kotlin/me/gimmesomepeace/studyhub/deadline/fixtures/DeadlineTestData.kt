package me.gimmesomepeace.studyhub.deadline.fixtures

import org.junit.jupiter.params.provider.Arguments
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID
import java.util.stream.Stream

@Suppress("unused")
object DeadlineTestData {
    @JvmStatic
    fun invalidCreateDeadlineRequests(): Stream<Arguments> = Stream.of(
        Arguments.of(createDeadlineRequestMap(subjectId = null)),
        Arguments.of(createDeadlineRequestMap(title = null)),
        Arguments.of(createDeadlineRequestMap(dueAt = null)),
        Arguments.of(createDeadlineRequestMap(type = null)),
        Arguments.of(createDeadlineRequestMap(title = "")),
        Arguments.of(createDeadlineRequestMap(title = "    ")),
        Arguments.of(createDeadlineRequestMap(title = "A".repeat(250))),
        Arguments.of(createDeadlineRequestMap(type = "INVALID_TYPE")),
        Arguments.of(createDeadlineRequestMap(notes = "A".repeat(2100))),
    )

    @JvmStatic
    fun invalidUpdateDeadlineRequests(): Stream<Arguments> = Stream.of(
        Arguments.of(updateDeadlineRequestMap(title = "")),
        Arguments.of(updateDeadlineRequestMap(title = "   ")),
        Arguments.of(updateDeadlineRequestMap(title = "A".repeat(250))),
        Arguments.of(updateDeadlineRequestMap(type = "INVALID_TYPE")),
        Arguments.of(updateDeadlineRequestMap(notes = "A".repeat(2100))),
    )

    private fun createDeadlineRequestMap(
        subjectId: Any? = UUID.randomUUID().toString(),
        componentId: Any? = null,
        title: String? = "Лабораторная работа 1",
        dueAt: Any? = Instant.now().plus(7, ChronoUnit.DAYS).toString(),
        type: Any? = "LAB",
        notes: String? = "Сдать через LMS",
    ): Map<String, Any?> = mapOf(
        "subjectId" to subjectId,
        "componentId" to componentId,
        "title" to title,
        "dueAt" to dueAt,
        "type" to type,
        "notes" to notes,
    )

    private fun updateDeadlineRequestMap(
        subjectId: Any? = null,
        componentId: Any? = null,
        title: String? = null,
        dueAt: Any? = null,
        type: Any? = null,
        notes: String? = null,
    ): Map<String, Any?> = mapOf(
        "subjectId" to subjectId,
        "componentId" to componentId,
        "title" to title,
        "dueAt" to dueAt,
        "type" to type,
        "notes" to notes,
    )
}
