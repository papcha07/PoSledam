package com.example.alinaposledam.profileTest

import domain.interactor.announcement.AnnouncementInteractor
import domain.model.AnnouncementInfo
import domain.model.AnnouncementStatus
import domain.model.CancelReason
import domain.model.FoundReport
import domain.model.ProfileAnnouncementDetails
import domain.model.SpottedLocation
import kotlinx.coroutines.CompletableDeferred
import model.InternetStatus
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

    override suspend fun cancelAnnouncement(
        cancelReason: CancelReason
    ): Pair<Boolean, InternetStatus?> {
        return true to null
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

    override suspend fun getAnnouncementDetails(
        id: String,
        type: Int
    ): Pair<ProfileAnnouncementDetails?, InternetStatus?> {
        return Pair(null, InternetStatus.Error)
    }

    override suspend fun getSpottedLocations(
        announcementId: String
    ): Pair<List<SpottedLocation>?, InternetStatus?> {
        return Pair(emptyList(), null)
    }

    override suspend fun getFoundReports(
        announcementId: String
    ): Pair<List<FoundReport>?, InternetStatus?> {
        return Pair(emptyList(), null)
    }
}
