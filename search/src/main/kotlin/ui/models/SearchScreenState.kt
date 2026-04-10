package ui.models

sealed class SearchState {
    object Idle : SearchState()
    object Loading : SearchState()
    object Success : SearchState()
    data class Error(val message: String) : SearchState()
}
