package model.errorResponse

import kotlinx.serialization.Serializable

@Serializable
data class ErrorDetails(
    val code: String? = null,
    val message: String? = null,
    val details: List<ErrorInfo>? = null
)
