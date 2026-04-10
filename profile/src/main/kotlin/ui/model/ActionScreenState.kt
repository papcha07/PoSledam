package ui.model

sealed class ActionScreenState {
    data object Idle: ActionScreenState()
    data object Loading: ActionScreenState()
    data object SuccessAction : ActionScreenState()
    data class FailedAction(val message: String): ActionScreenState()
    data object FillAllField : ActionScreenState()
}