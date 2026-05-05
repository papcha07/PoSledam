package ui.profile

data class PersonDto(
    val name: String,
    val uri: String? = null,
    val description: String? = null,
    val vkUri: String? = null,
    val tgUri: String? = null
)
