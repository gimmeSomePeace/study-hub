package me.gimmesomepeace.studyhub.common.validation

import jakarta.validation.Constraint
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [NotBlankIfPresentValidator::class])
annotation class NotBlankIfPresent(
    val message: String = "Must be not blank",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Any>> = []
)
