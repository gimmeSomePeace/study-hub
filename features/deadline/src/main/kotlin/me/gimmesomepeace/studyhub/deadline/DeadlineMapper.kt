package me.gimmesomepeace.studyhub.deadline

import me.gimmesomepeace.studyhub.deadline.dto.DeadlineAction
import me.gimmesomepeace.studyhub.deadline.dto.DeadlineDetails
import me.gimmesomepeace.studyhub.deadline.dto.DeadlineListItem
import me.gimmesomepeace.studyhub.deadline.dto.DeadlineStatus
import me.gimmesomepeace.studyhub.deadline.entity.DeadlineEntity
import java.util.UUID

fun DeadlineEntity.toDetails(actions: List<DeadlineAction>) = DeadlineDetails(
    id = id,
    subjectId = subjectId,
    componentId = componentId,
    status = status,
    type = type,
    title = title,
    dueAt = dueAt,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt,
    actions = actions,
)

fun DeadlineEntity.toListItem() = DeadlineListItem(
    id = id,
    subjectId = subjectId,
    componentId = componentId,
    title = title,
    status = status,
    type = type,
    dueAt = dueAt,
)

/*
 * Маппит целевой статус дедлайна в действие, которое нужно выполнить
 * для перехода в этот статус.
 *
 * Пример:
 * - Целевой статус [DeadlineStatus.CLOSED] → действие `CLOSE` (`POST /deadlines/{id}/close`)
 *
 * @param deadlineId идентификатор дедлайна, для которого строится действие
 * @return описание действия
 */
fun DeadlineStatus.toActionAsTargetStatus(deadlineId: UUID) = when (this) {
    DeadlineStatus.OPEN -> DeadlineAction(
        name = "REOPEN",
        method = "POST",
        href = "/api/v1/deadlines/$deadlineId/reopen",
        description = "Открыть заново",
    )

    DeadlineStatus.CLOSED -> DeadlineAction(
        name = "CLOSE",
        method = "POST",
        href = "/api/v1/deadlines/$deadlineId/close",
        description = "Закрыть",
    )

    DeadlineStatus.CANCELLED -> DeadlineAction(
        name = "CANCEL",
        method = "POST",
        href = "/api/v1/deadlines/$deadlineId/cancel",
        description = "Отменить",
    )
}
