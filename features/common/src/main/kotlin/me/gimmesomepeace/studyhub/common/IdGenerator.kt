package me.gimmesomepeace.studyhub.common

fun interface IdGenerator<T> {
    fun generate(): T
}
