package domain.model

import AnnouncementType
import apiService.models.announcement_models.CancelAnnouncementRequest

data class CancelReason(
    val id: String,
    val reason: Int,
    val type: Int
)


fun CancelReason.toCancelAnnouncementRequest(): CancelAnnouncementRequest {
    return CancelAnnouncementRequest(
        id = this.id,
        cancelReason = this.reason
    )
}

fun Int.toMethodType(): AnnouncementType {
    return if (this == 0) AnnouncementType.Miss else AnnouncementType.Found
}
