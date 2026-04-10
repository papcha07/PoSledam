package com.example.alinaposledam.profileTest

import domain.interactor.AnnouncementInteractor
import domain.model.AnnouncementInfo
import domain.model.AnnouncementStatus
import kotlinx.coroutines.CompletableDeferred
import ui.model.PetUiPreview

class FakeAnnouncementInteractor : AnnouncementInteractor {

    var getUserAnnouncementsResult: Pair<List<PetUiPreview>?, Int?> = Pair(emptyList(), null)
    var gate: CompletableDeferred<Unit>? = null

    fun returnSuccess(list: List<PetUiPreview>) {
        getUserAnnouncementsResult = Pair(list, null)
    }

    fun returnFailed() {
        getUserAnnouncementsResult = Pair(null, 400)
    }

    fun returnsEmpty() {
        getUserAnnouncementsResult = Pair(listOf(), null)
    }

    override suspend fun sendAnnouncement(
        announcementInfo: AnnouncementInfo,
        files: List<String>,
        type: Int
    ): AnnouncementStatus {
        return AnnouncementStatus.Success
    }

    override suspend fun getUserAnnouncements(type: Int): Pair<List<PetUiPreview>?, Int?> {
        gate?.await()
        return getUserAnnouncementsResult
    }
}