package domain.interactor.announcement

import domain.model.AnnouncementInfo
import domain.model.AnnouncementStatus
import domain.model.CancelReason
import domain.model.ProfileAnnouncementDetails
import domain.model.SpottedLocation
import domain.repository.AnnouncementRepository
import model.InternetStatus
import ui.model.PetUiPreview

class AnnouncementInteractorImpl(
    private val announcementRepository: AnnouncementRepository
) : AnnouncementInteractor {
    override suspend fun cancelAnnouncement(cancelReason: CancelReason): Pair<Boolean, InternetStatus?> {
        return announcementRepository.cancelAnnouncement(cancelReason)
    }

    override suspend fun sendAnnouncement(
        announcementInfo: AnnouncementInfo,
        files: List<String>,
        type: Int
    ): AnnouncementStatus =
        announcementRepository.sendAnnouncement(announcementInfo, files, type)

    override suspend fun getUserAnnouncements(type: Int): Pair<List<PetUiPreview>?, Int?> {
        return announcementRepository.getUserAnnouncements(type)
    }

    override suspend fun getAnnouncementDetails(
        id: String,
        type: Int
    ): Pair<ProfileAnnouncementDetails?, InternetStatus?> {
        return announcementRepository.getAnnouncementDetails(id, type)
    }

    override suspend fun getSpottedLocations(
        announcementId: String
    ): Pair<List<SpottedLocation>?, InternetStatus?> {
        return announcementRepository.getSpottedLocations(announcementId)
    }

}
