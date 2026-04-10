package domain.model

import model.InternetStatus

sealed class AnnouncementStatus {
    data object Success : AnnouncementStatus()
    data class Failed(val message: InternetStatus) : AnnouncementStatus()
}