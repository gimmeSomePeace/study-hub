package me.gimmesomepeace.studyhub.deadline.repository

import me.gimmesomepeace.studyhub.deadline.entity.DeadlineEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DeadlineRepository : JpaRepository<DeadlineEntity, UUID>
