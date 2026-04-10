package model.errorResponse

import kotlinx.serialization.Serializable

@Serializable
data class ErrorDetails(
    val code: String,
    val message: String,
    val details: List<ErrorInfo>
)