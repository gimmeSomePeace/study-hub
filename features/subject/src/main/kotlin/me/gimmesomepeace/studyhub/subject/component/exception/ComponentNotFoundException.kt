package me.gimmesomepeace.studyhub.subject.component.exception

import java.util.UUID

class ComponentNotFoundException(
    val id: UUID,
) : RuntimeException("Subject component with id '$id' not found")
