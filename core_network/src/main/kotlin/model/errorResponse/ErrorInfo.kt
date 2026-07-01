package model.errorResponse

import kotlinx.serialization.Serializable

@Serializable
data class ErrorInfo(
    val field: String? = null,
    val issue: String? = null,
    val message: String? = null
)
