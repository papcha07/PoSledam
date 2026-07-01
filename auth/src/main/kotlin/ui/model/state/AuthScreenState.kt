package ui.model.state

sealed class AuthScreenState {
    data object Idle : AuthScreenState()
    object Loading : AuthScreenState()
    object Success : AuthScreenState()
    data class Error(val message: String) : AuthScreenState()
    data class EmailNotConfirmed(val email: String) : AuthScreenState()
}
