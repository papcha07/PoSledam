package model.auth.response

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val avatarPath: String? = null,
    val contacts: List<Contact>? = null,
    val email: String,
    val firstName: String,
    val id: String,
    val patronymic: String? = null,
    val secondName: String? = null
)