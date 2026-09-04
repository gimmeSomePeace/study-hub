package me.gimmesomepeace.studyhub.core.token

@JvmInline
value class AccessToken(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "AccessToken must not be blank" }
    }
}
