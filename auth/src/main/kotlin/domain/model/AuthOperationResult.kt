package domain.model

sealed class AuthOperationResult {
    data object Success : AuthOperationResult()
    data class Error(val type: AuthOperationErrorType) : AuthOperationResult()
}

enum class AuthOperationErrorType {
    TooManyEmailConfirmationRequests,
    NoInternet,
    NotFound,
    Unknown
}
