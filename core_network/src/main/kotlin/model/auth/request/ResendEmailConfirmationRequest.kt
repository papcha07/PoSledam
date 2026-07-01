package model.auth.request

import kotlinx.serialization.Serializable

@Serializable
data class ResendEmailConfirmationRequest(
    val email: String
)
