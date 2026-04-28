package ui.model

sealed class RegisterScreenState {
    data object Idle : RegisterScreenState()
    object Loading : RegisterScreenState()
    object Success : RegisterScreenState()
}