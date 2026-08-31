package me.gimmesomepeace.studyhub.deadline

import me.gimmesomepeace.studyhub.deadline.dto.DeadlineStatus

object DeadlineConstraints {
    const val TITLE_MIN_LENGTH = 1
    const val TITLE_MAX_LENGTH = 200

    const val NOTES_MAX_LENGTH = 2000
    val DEFAULT_STATUS = DeadlineStatus.OPEN
}
