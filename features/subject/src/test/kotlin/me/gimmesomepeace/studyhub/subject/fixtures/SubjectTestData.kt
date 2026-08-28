package me.gimmesomepeace.studyhub.subject.fixtures

import org.junit.jupiter.params.provider.Arguments
import java.util.UUID
import java.util.stream.Stream

@Suppress("unused")
object SubjectTestData {
    @JvmStatic
    fun invalidCreateSubjectRequests(): Stream<Arguments> = Stream.of(
        Arguments.of(createSemesterRequestMap(semesterId = null)),
        Arguments.of(createSemesterRequestMap(name = "")),
        Arguments.of(createSemesterRequestMap(name = "    ")),
        Arguments.of(createSemesterRequestMap(name = "A".repeat(250))),
        Arguments.of(createSemesterRequestMap(code = "A")),
        Arguments.of(createSemesterRequestMap(code = "A".repeat(250))),
        Arguments.of(createSemesterRequestMap(color = "blue")),
        Arguments.of(createSemesterRequestMap(color = "#6666666")),
    )

    @JvmStatic
    fun invalidUpdateSubjectRequests(): Stream<Arguments> = Stream.of(
        Arguments.of(updateSemesterRequestMap(name = "")),
        Arguments.of(updateSemesterRequestMap(name = "   ")),
        Arguments.of(updateSemesterRequestMap(name = "A".repeat(250))),
        Arguments.of(updateSemesterRequestMap(code = "A")),
        Arguments.of(updateSemesterRequestMap(code = "A".repeat(250))),
        Arguments.of(updateSemesterRequestMap(color = "blue")),
        Arguments.of(updateSemesterRequestMap(color = "#0000")),
    )

    private fun createSemesterRequestMap(
        semesterId: UUID? = semesterId(),
        name: String? = "Алгоритмы",
        code: String? = "CS101",
        teacher: String? = "Иванов И.И.",
        color: String? = "#3B82F6",
    ): Map<String, Any?> = mapOf(
        "semesterId" to semesterId,
        "name" to name,
        "code" to code,
        "teacher" to teacher,
        "color" to color,
    )

    private fun updateSemesterRequestMap(
        semesterId: UUID? = null,
        name: String? = null,
        code: String? = null,
        teacher: String? = null,
        color: String? = null,
    ): Map<String, Any?> = mapOf(
        "semesterId" to semesterId,
        "name" to name,
        "code" to code,
        "teacher" to teacher,
        "color" to color,
    )
}
