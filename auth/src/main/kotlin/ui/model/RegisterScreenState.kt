package ui.model
sealed class RegisterScreenState {
    object Loading : RegisterScreenState()
    object Success : RegisterScreenState()
    data class Error(val message: String) : RegisterScreenState()
}