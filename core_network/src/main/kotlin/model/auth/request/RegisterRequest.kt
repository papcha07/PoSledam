package model.auth.request

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val description: String? = null,
    val contacts: List<SocialMedia>? = null
)

@Serializable
data class SocialMedia(
    val contactType: Int,
    val url: String
)