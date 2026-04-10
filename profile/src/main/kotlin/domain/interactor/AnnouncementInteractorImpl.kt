package domain.interactor

import domain.model.AnnouncementInfo
import domain.model.AnnouncementStatus
import domain.repository.AnnouncementRepository
import ui.model.PetUiPreview

class AnnouncementInteractorImpl(
    private val announcementRepository: AnnouncementRepository
) : AnnouncementInteractor {
    override suspend fun sendAnnouncement(
        announcementInfo: AnnouncementInfo,
        files: List<String>,
        type: Int
    ): AnnouncementStatus =
        announcementRepository.sendAnnouncement(announcementInfo, files, type)

    override suspend fun getUserAnnouncements(type: Int): Pair<List<PetUiPreview>?, Int?> {
        return announcementRepository.getUserAnnouncements(type)
    }

}