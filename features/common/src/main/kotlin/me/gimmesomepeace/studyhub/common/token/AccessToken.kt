package me.gimmesomepeace.studyhub.common.token

@JvmInline
value class AccessToken(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "AccessToken must not be blank" }
    }
}
