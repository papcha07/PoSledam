package domain.model

sealed class AuthResult {
    data object Success : AuthResult()
    data class Failed(val message: String) : AuthResult()
}