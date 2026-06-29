package domain.interactor.announcement

import domain.model.AnnouncementInfo
import domain.model.AnnouncementStatus
import domain.model.CancelReason
import domain.model.FoundReport
import domain.model.Location
import domain.model.ProfileAnnouncementDetails
import domain.model.SpottedLocation
import domain.repository.AnnouncementRepository
import kotlinx.coroutines.test.runTest
import model.InternetStatus
import org.junit.Assert.assertEquals
import org.junit.Test
import ui.model.PetUiPreview
import java.time.LocalDate
import java.time.LocalTime

class AnnouncementInteractorImplTest {

    private val repository = FakeAnnouncementRepository()
    private val interactor = AnnouncementInteractorImpl(repository)

    @Test
    fun `getUserAnnouncements delegates selected type to repository`() = runTest {
        val expected = listOf(
            PetUiPreview(
                id = "pet-id",
                breed = "корги",
                description = "нашли",
                district = "Центральный",
                imageUrl = "image.jpg"
            )
        )
        repository.userAnnouncementsResult = expected to null

        val result = interactor.getUserAnnouncements(type = 1)

        assertEquals(1, repository.lastUserAnnouncementsType)
        assertEquals(expected, result.first)
        assertEquals(null, result.second)
    }

    @Test
    fun `sendAnnouncement passes announcement files and type unchanged`() = runTest {
        val announcement = AnnouncementInfo(
            location = Location(latitude = 56.0, longitude = 92.0),
            petType = 1,
            gender = 0,
            color = "рыжий",
            breed = "корги",
            petName = "Бублик",
            eventDate = LocalDate.of(2026, 6, 23),
            time = LocalTime.of(12, 30),
            description = "убежал"
        )
        val files = listOf("first.jpg", "second.jpg")
        repository.sendAnnouncementResult = AnnouncementStatus.Success

        val result = interactor.sendAnnouncement(
            announcementInfo = announcement,
            files = files,
            type = 0
        )

        assertEquals(AnnouncementStatus.Success, result)
        assertEquals(announcement, repository.lastAnnouncementInfo)
        assertEquals(files, repository.lastFiles)
        assertEquals(0, repository.lastSendType)
    }

    @Test
    fun `details related methods return repository results`() = runTest {
        val details = ProfileAnnouncementDetails(
            id = "announcement-id",
            imagePath = "image.jpg",
            petType = 1,
            gender = 0,
            color = "рыжий",
            breed = "корги",
            description = "описание",
            district = "Центральный",
            street = "Ленина",
            house = "10",
            latitude = 56.0,
            longitude = 92.0,
            eventDate = "23.06.2026",
            eventTime = "12:30"
        )
        val spot = SpottedLocation(
            id = "spot-id",
            spottedUserName = "Иван",
            createdDate = "23.06.2026",
            createdTime = "12:30",
            latitude = 56.01,
            longitude = 92.01,
            imagesPath = emptyList()
        )
        val report = FoundReport(
            id = "report-id",
            user = domain.model.FoundReportUser(
                id = "user-id",
                name = "Мария",
                contacts = emptyList()
            ),
            createdDate = "23.06.2026",
            createdTime = "12:30",
            imagesPath = emptyList()
        )
        repository.detailsResult = details to null
        repository.spottedLocationsResult = listOf(spot) to null
        repository.foundReportsResult = listOf(report) to null

        assertEquals(details, interactor.getAnnouncementDetails("announcement-id", 0).first)
        assertEquals(listOf(spot), interactor.getSpottedLocations("announcement-id").first)
        assertEquals(listOf(report), interactor.getFoundReports("announcement-id").first)
        assertEquals("announcement-id", repository.lastDetailsId)
        assertEquals(0, repository.lastDetailsType)
        assertEquals("announcement-id", repository.lastSpottedAnnouncementId)
        assertEquals("announcement-id", repository.lastFoundReportsAnnouncementId)
    }

    @Test
    fun `cancelAnnouncement returns repository error result`() = runTest {
        val reason = CancelReason(id = "announcement-id", reason = 2, type = 0)
        repository.cancelResult = false to InternetStatus.NoInternet

        val result = interactor.cancelAnnouncement(reason)

        assertEquals(reason, repository.lastCancelReason)
        assertEquals(false, result.first)
        assertEquals(InternetStatus.NoInternet, result.second)
    }
}

private class FakeAnnouncementRepository : AnnouncementRepository {
    var userAnnouncementsResult: Pair<List<PetUiPreview>?, Int?> = null to -1
    var sendAnnouncementResult: AnnouncementStatus = AnnouncementStatus.Failed(InternetStatus.Error)
    var cancelResult: Pair<Boolean, InternetStatus?> = false to InternetStatus.Error
    var detailsResult: Pair<ProfileAnnouncementDetails?, InternetStatus?> = null to InternetStatus.Error
    var spottedLocationsResult: Pair<List<SpottedLocation>?, InternetStatus?> = null to InternetStatus.Error
    var foundReportsResult: Pair<List<FoundReport>?, InternetStatus?> = null to InternetStatus.Error

    var lastUserAnnouncementsType: Int? = null
    var lastAnnouncementInfo: AnnouncementInfo? = null
    var lastFiles: List<String>? = null
    var lastSendType: Int? = null
    var lastCancelReason: CancelReason? = null
    var lastDetailsId: String? = null
    var lastDetailsType: Int? = null
    var lastSpottedAnnouncementId: String? = null
    var lastFoundReportsAnnouncementId: String? = null

    override suspend fun cancelAnnouncement(
        cancelReason: CancelReason
    ): Pair<Boolean, InternetStatus?> {
        lastCancelReason = cancelReason
        return cancelResult
    }

    override suspend fun sendAnnouncement(
        announcementInfo: AnnouncementInfo,
        files: List<String>,
        type: Int
    ): AnnouncementStatus {
        lastAnnouncementInfo = announcementInfo
        lastFiles = files
        lastSendType = type
        return sendAnnouncementResult
    }

    override suspend fun getUserAnnouncements(type: Int): Pair<List<PetUiPreview>?, Int?> {
        lastUserAnnouncementsType = type
        return userAnnouncementsResult
    }

    override suspend fun getAnnouncementDetails(
        id: String,
        type: Int
    ): Pair<ProfileAnnouncementDetails?, InternetStatus?> {
        lastDetailsId = id
        lastDetailsType = type
        return detailsResult
    }

    override suspend fun getSpottedLocations(
        announcementId: String
    ): Pair<List<SpottedLocation>?, InternetStatus?> {
        lastSpottedAnnouncementId = announcementId
        return spottedLocationsResult
    }

    override suspend fun getFoundReports(
        announcementId: String
    ): Pair<List<FoundReport>?, InternetStatus?> {
        lastFoundReportsAnnouncementId = announcementId
        return foundReportsResult
    }
}
