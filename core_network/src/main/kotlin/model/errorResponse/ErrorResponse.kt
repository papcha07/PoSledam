package model.errorResponse

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val error: ErrorDetails? = null,
    val code: String? = null,
    val message: String? = null,
    val details: List<ErrorInfo>? = null
) {
    fun asErrorDetails(): ErrorDetails? {
        return error ?: if (code != null || message != null || details != null) {
            ErrorDetails(
                code = code,
                message = message,
                details = details
            )
        } else {
            null
        }
    }
}
