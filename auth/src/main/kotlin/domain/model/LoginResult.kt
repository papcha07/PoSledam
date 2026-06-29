package domain.model

sealed class LoginResult {
    data object Success : LoginResult()
    data class Error(
        val type: LoginErrorType,
        val message: String? = null
    ) : LoginResult()
}

enum class LoginErrorType {
    EmailNotConfirmed,
    InvalidCredentials,
    NoInternet,
    Server,
    Unknown
}
