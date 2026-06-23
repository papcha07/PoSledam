package ui.viewModel

import domain.interactor.announcement.AnnouncementInteractor
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import testutils.MainDispatcherRule
import ui.model.PetUiPreview

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val announcementInteractor: AnnouncementInteractor = mockk()

    @Test
    fun `updateMethodValue changes selected announcement type`() {
        val viewModel = ProfileViewModel(announcementInteractor)

        viewModel.updateMethodValue(1)

        assertEquals(1, viewModel.userMethodState.value)
    }

    @Test
    fun `getAnimalList sets success when interactor returns pets`() = runTest {
        val pets = listOf(
            PetUiPreview(
                id = "pet-id",
                breed = "корги",
                description = "потерялся",
                district = "Центральный",
                imageUrl = "image.jpg"
            )
        )
        coEvery { announcementInteractor.getUserAnnouncements(0) } returns (pets to null)
        val viewModel = ProfileViewModel(announcementInteractor)

        viewModel.getAnimalList()
        advanceUntilIdle()

        assertEquals(ProfileScreenState.Success(pets), viewModel.userPetState.value)
    }

    @Test
    fun `getAnimalList sets empty when interactor returns empty list`() = runTest {
        coEvery { announcementInteractor.getUserAnnouncements(0) } returns (emptyList<PetUiPreview>() to null)
        val viewModel = ProfileViewModel(announcementInteractor)

        viewModel.getAnimalList()
        advanceUntilIdle()

        assertEquals(ProfileScreenState.Empty, viewModel.userPetState.value)
    }

    @Test
    fun `getAnimalList uses selected type and sets failed on error`() = runTest {
        coEvery { announcementInteractor.getUserAnnouncements(1) } returns (null to -1)
        val viewModel = ProfileViewModel(announcementInteractor)
        viewModel.updateMethodValue(1)

        viewModel.getAnimalList()
        advanceUntilIdle()

        assertEquals(ProfileScreenState.Failed, viewModel.userPetState.value)
    }
}
