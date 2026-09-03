package me.gimmesomepeace.studyhub.common.id

fun interface IdGenerator<T> {
    fun generate(): T
}
