package apiService.models.announcement_models

data class CancelAnnouncementRequest(
    val id: String,
    val cancelReason: Int
)