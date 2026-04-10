package domain.repository

import domain.model.AnnouncementInfo
import domain.model.AnnouncementStatus
import kotlinx.coroutines.flow.Flow
import ui.model.PetUiPreview

interface AnnouncementRepository {
    suspend fun sendAnnouncement(
        announcementInfo: AnnouncementInfo,
        files: List<String>,
        type: Int
    ): AnnouncementStatus

    suspend fun getUserAnnouncements(type: Int): Pair<List<PetUiPreview>?, Int?>


}
