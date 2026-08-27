package me.gimmesomepeace.studyhub.semester.repository

import me.gimmesomepeace.studyhub.semester.entity.SemesterEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SemesterRepository : JpaRepository<SemesterEntity, UUID>
