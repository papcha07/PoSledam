package ui.model

sealed class AnnouncementScreenState {
    data object Idle : AnnouncementScreenState()
    data object Failed : AnnouncementScreenState()
    data object Success : AnnouncementScreenState()
}
