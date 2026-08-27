package me.gimmesomepeace.studyhub.semester.api

import org.junit.jupiter.params.provider.Arguments
import java.time.LocalDate
import java.util.stream.Stream

@Suppress("unused")
object SemesterTestData {
    @JvmStatic
    fun invalidCreateSemesterRequests(): Stream<Arguments> = Stream.of(
        Arguments.of(createSemesterRequestMap(name = null)),
        Arguments.of(createSemesterRequestMap(name = "     ")),
        Arguments.of(createSemesterRequestMap(name = "A".repeat(200))),
        Arguments.of(createSemesterRequestMap(startsAt = null)),
        Arguments.of(createSemesterRequestMap(endsAt = null)),
        Arguments.of(createSemesterRequestMap(startsAt = LocalDate.of(2020, 1, 1), endsAt = LocalDate.of(2019, 1, 1))),
    )

    @JvmStatic
    fun invalidUpdateSemesterRequests(): Stream<Arguments> = Stream.of(
        Arguments.of(createSemesterRequestMap(name = "     ")),
        Arguments.of(createSemesterRequestMap(name = "A".repeat(200))),
        Arguments.of(createSemesterRequestMap(startsAt = LocalDate.of(2020, 1, 1), endsAt = LocalDate.of(2019, 1, 1))),
    )

    private fun createSemesterRequestMap(
        name: String? = "NEW SEMESTER",
        startsAt: LocalDate? = LocalDate.of(2020, 1, 1),
        endsAt: LocalDate? = LocalDate.of(2021, 1, 1),
    ): Map<String, Any?> = mapOf(
        "name" to name,
        "startsAt" to startsAt,
        "endsAt" to endsAt,
    )

    private fun updateSemesterRequestMap(
        name: String?,
        startsAt: LocalDate?,
        endsAt: LocalDate?,
    ): Map<String, Any?> = mapOf(
        "name" to name,
        "startsAt" to startsAt,
        "endsAt" to endsAt,
    )
}
