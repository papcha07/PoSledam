package domain.repository

import domain.model.AnnouncementInfo
import domain.model.AnnouncementStatus
import domain.model.ProfileAnnouncementDetails
import domain.model.SpottedLocation
import model.InternetStatus
import ui.model.PetUiPreview

interface AnnouncementRepository {

    suspend fun cancelAnnouncement(
        cancelReason: CancelReason
    ): Pair<Boolean, InternetStatus?>

    suspend fun sendAnnouncement(
        announcementInfo: AnnouncementInfo,
        files: List<String>,
        type: Int
    ): AnnouncementStatus

    suspend fun getUserAnnouncements(type: Int): Pair<List<PetUiPreview>?, Int?>

    suspend fun getAnnouncementDetails(
        id: String,
        type: Int
    ): Pair<ProfileAnnouncementDetails?, InternetStatus?>

    suspend fun getSpottedLocations(
        announcementId: String
    ): Pair<List<SpottedLocation>?, InternetStatus?>

}
