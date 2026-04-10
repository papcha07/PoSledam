package domain.model

sealed class LoginResult {
    data class Success(val token: String) : LoginResult()
    data class Failed(val message: String) : LoginResult()
}