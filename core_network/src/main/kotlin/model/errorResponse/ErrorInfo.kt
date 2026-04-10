package model.errorResponse

import kotlinx.serialization.Serializable

@Serializable
data class ErrorInfo(
    val field: String,
    val issue: String,
    val message: String
)
