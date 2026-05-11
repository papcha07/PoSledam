package ui.model

sealed class ScreenState<out T> {
    data object Idle : ScreenState<Nothing>()
    data object Loading : ScreenState<Nothing>()
    data class Success<out T>(val data: T) : ScreenState<T>()
    data object Error : ScreenState<Nothing>()
    data object InternetError : ScreenState<Nothing>()
}
