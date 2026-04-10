package model.errorResponse

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val error: ErrorDetails
)