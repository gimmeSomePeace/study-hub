package me.gimmesomepeace.studyhub.subject.component.fixtures

import org.junit.jupiter.params.provider.Arguments
import java.util.stream.Stream

@Suppress("unused")
object ComponentTestData {
    @JvmStatic
    fun invalidCreateComponentRequests(): Stream<Arguments> = Stream.of(
        Arguments.of(createComponentRequestMap(type = null)),
        Arguments.of(createComponentRequestMap(title = null)),
        Arguments.of(createComponentRequestMap(title = "")),
        Arguments.of(createComponentRequestMap(title = "    ")),
        Arguments.of(createComponentRequestMap(title = "A".repeat(250))),
        Arguments.of(createComponentRequestMap(type = "INVALID_TYPE")),
        Arguments.of(createComponentRequestMap(priority = 0)),
        Arguments.of(createComponentRequestMap(priority = 6)),
        Arguments.of(createComponentRequestMap(notes = "A".repeat(2100))),
    )

    @JvmStatic
    fun invalidUpdateComponentRequests(): Stream<Arguments> = Stream.of(
        Arguments.of(updateComponentRequestMap(title = "")),
        Arguments.of(updateComponentRequestMap(title = "   ")),
        Arguments.of(updateComponentRequestMap(title = "A".repeat(250))),
        Arguments.of(updateComponentRequestMap(priority = 0)),
        Arguments.of(updateComponentRequestMap(priority = 6)),
        Arguments.of(updateComponentRequestMap(notes = "A".repeat(2100))),
    )

    private fun createComponentRequestMap(
        type: Any? = "LECTURE",
        title: String? = "Лекция 1: Введение",
        priority: Int? = 3,
        notes: String? = "Базовые понятия",
    ): Map<String, Any?> = mapOf(
        "type" to type,
        "title" to title,
        "priority" to priority,
        "notes" to notes,
    )

    private fun updateComponentRequestMap(
        type: Any? = null,
        title: String? = null,
        priority: Int? = null,
        notes: String? = null,
    ): Map<String, Any?> = mapOf(
        "type" to type,
        "title" to title,
        "priority" to priority,
        "notes" to notes,
    )
}
