package me.gimmesomepeace.studyhub.core.id

fun interface IdGenerator<T> {
    fun generate(): T
}
