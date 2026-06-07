package apiService.models.announcement_models

import kotlinx.serialization.Serializable

@Serializable
data class CancelAnnouncementRequest(
    val id: String,
    val deleteReason: Int
)