package me.gimmesomepeace.studyhub.subject.repository

import me.gimmesomepeace.studyhub.subject.entity.SubjectEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SubjectRepository : JpaRepository<SubjectEntity, UUID>
