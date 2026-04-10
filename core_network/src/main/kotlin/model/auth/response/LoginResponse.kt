package model.auth.response

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String
)