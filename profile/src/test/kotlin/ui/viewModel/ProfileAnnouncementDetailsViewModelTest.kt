package ui.viewModel

import domain.interactor.announcement.AnnouncementInteractor
import domain.model.CancelReason
import domain.model.FoundReport
import domain.model.FoundReportUser
import domain.model.ProfileAnnouncementDetails
import domain.model.SpottedLocation
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import model.InternetStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import testutils.MainDispatcherRule

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileAnnouncementDetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val announcementInteractor: AnnouncementInteractor = mockk()

    @Test
    fun `loadDetails for missing announcement loads details spotted locations and found reports`() = runTest {
        val details = details()
        val spottedLocation = SpottedLocation(
            id = "spot-id",
            spottedUserName = "Иван",
            createdDate = "23.06.2026",
            createdTime = "12:30",
            latitude = 56.01,
            longitude = 92.01,
            imagesPath = listOf("spot.jpg")
        )
        val foundReport = FoundReport(
            id = "report-id",
            user = FoundReportUser(
                id = "user-id",
                name = "Мария",
                contacts = emptyList()
            ),
            createdDate = "23.06.2026",
            createdTime = "12:40",
            imagesPath = listOf("report.jpg")
        )
        coEvery { announcementInteractor.getAnnouncementDetails("announcement-id", 0) } returns (details to null)
        coEvery { announcementInteractor.getSpottedLocations("announcement-id") } returns (listOf(spottedLocation) to null)
        coEvery { announcementInteractor.getFoundReports("announcement-id") } returns (listOf(foundReport) to null)
        val viewModel = ProfileAnnouncementDetailsViewModel(announcementInteractor)

        viewModel.loadDetails(announcementId = "announcement-id", announcementType = 0)
        advanceUntilIdle()

        val state = viewModel.detailsState.value as ProfileAnnouncementDetailsState.Success
        assertEquals(details, state.announcement)
        assertEquals(listOf(spottedLocation), state.spottedLocations)
        assertEquals(listOf(foundReport), state.foundReports)
        assertNull(state.spottedLocationsError)
        assertNull(state.foundReportsError)
    }

    @Test
    fun `loadDetails for found announcement does not load missing announcement extras`() = runTest {
        val details = details()
        coEvery { announcementInteractor.getAnnouncementDetails("announcement-id", 1) } returns (details to null)
        val viewModel = ProfileAnnouncementDetailsViewModel(announcementInteractor)

        viewModel.loadDetails(announcementId = "announcement-id", announcementType = 1)
        advanceUntilIdle()

        val state = viewModel.detailsState.value as ProfileAnnouncementDetailsState.Success
        assertEquals(details, state.announcement)
        assertEquals(emptyList<SpottedLocation>(), state.spottedLocations)
        assertEquals(emptyList<FoundReport>(), state.foundReports)
        coVerify(exactly = 0) { announcementInteractor.getSpottedLocations(any()) }
        coVerify(exactly = 0) { announcementInteractor.getFoundReports(any()) }
    }

    @Test
    fun `loadDetails sets failed message when details request has no internet`() = runTest {
        coEvery {
            announcementInteractor.getAnnouncementDetails("announcement-id", 0)
        } returns (null to InternetStatus.NoInternet)
        val viewModel = ProfileAnnouncementDetailsViewModel(announcementInteractor)

        viewModel.loadDetails(announcementId = "announcement-id", announcementType = 0)
        advanceUntilIdle()

        assertEquals(
            ProfileAnnouncementDetailsState.Failed("Проблемы с интернетом"),
            viewModel.detailsState.value
        )
    }

    @Test
    fun `cancelAnnouncement exposes success and can clear result`() = runTest {
        val reason = CancelReason(id = "announcement-id", reason = 2, type = 0)
        coEvery { announcementInteractor.cancelAnnouncement(reason) } returns (true to null)
        val viewModel = ProfileAnnouncementDetailsViewModel(announcementInteractor)

        viewModel.cancelAnnouncement(
            reasonId = 2,
            announcementType = 0,
            announcementId = "announcement-id"
        )
        advanceUntilIdle()

        assertTrue(viewModel.cancelState.value.isSuccess)
        assertFalse(viewModel.cancelState.value.isLoading)

        viewModel.clearCancelResult()

        assertEquals(CancelAnnouncementState(), viewModel.cancelState.value)
    }

    @Test
    fun `cancelAnnouncement exposes error and can clear error`() = runTest {
        val reason = CancelReason(id = "announcement-id", reason = 2, type = 0)
        coEvery { announcementInteractor.cancelAnnouncement(reason) } returns (false to InternetStatus.Error)
        val viewModel = ProfileAnnouncementDetailsViewModel(announcementInteractor)

        viewModel.cancelAnnouncement(
            reasonId = 2,
            announcementType = 0,
            announcementId = "announcement-id"
        )
        advanceUntilIdle()

        assertEquals("Что-то пошло не так", viewModel.cancelState.value.errorMessage)

        viewModel.clearCancelError()

        assertNull(viewModel.cancelState.value.errorMessage)
    }

    private fun details() = ProfileAnnouncementDetails(
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
}
