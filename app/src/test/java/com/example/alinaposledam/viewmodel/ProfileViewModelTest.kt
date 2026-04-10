package com.example.alinaposledam.viewmodel

import domain.interactor.AnnouncementInteractor
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ui.model.PetUiPreview
import ui.viewModel.ProfileScreenState
import ui.viewModel.ProfileViewModel
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {
    private lateinit var profileViewModel: ProfileViewModel
    private val announcementInteractor: AnnouncementInteractor = mockk()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()


    @Before
    fun setUp() {
        profileViewModel = ProfileViewModel(
            announcementInteractor = announcementInteractor
        )
    }

    @Test
    fun selectLostPetButton() {
        val methodValueFlow: StateFlow<Int> = profileViewModel.userMethodState
        profileViewModel.updateMethodValue(0)
        assertEquals(0, methodValueFlow.value)
    }

    @Test
    fun selectLostPetButtonAndSelectMiss() {
        val methodValueFlow: StateFlow<Int> = profileViewModel.userMethodState
        profileViewModel.updateMethodValue(0)
        assertEquals(0, methodValueFlow.value)
        profileViewModel.updateMethodValue(1)
        assertEquals(1, methodValueFlow.value)
    }

    @Test
    fun selectMissPetButton() {
        val methodValueFlow: StateFlow<Int> = profileViewModel.userMethodState
        assertEquals(0, methodValueFlow.value)
        profileViewModel.updateMethodValue(1)
        assertEquals(1, methodValueFlow.value)
    }

    @Test
    fun `loadUserAnnouncements updates states with pets on success`() = runTest {
        val pets = listOf(
            PetUiPreview(
                id = "1",
                breed = "Сиамская",
                description = "Очень ласковая кошка",
                district = "Свердловская",
                imageUrl = "asdasdasd"
            ),
            PetUiPreview(
                id = "1",
                breed = "Сиамская",
                description = "Очень ласковая кошка",
                district = "Свердловская",
                imageUrl = "asdasdasd"
            ),
            PetUiPreview(
                id = "1",
                breed = "Сиамская",
                description = "Очень ласковая кошка",
                district = "Свердловская",
                imageUrl = "asdasdasd"
            )
        )

        coEvery { announcementInteractor.getUserAnnouncements(type = 0) } returns Pair(pets, null)
        profileViewModel = ProfileViewModel(announcementInteractor)
        profileViewModel.getAnimalList()
        val state = profileViewModel.userPetState.value
        assertEquals(state, ProfileScreenState.Success(pets))
    }

    @Test
    fun `loadUserAnnouncements updates states with emptyList`() = runTest {
        val pets = emptyList<PetUiPreview>()
        coEvery {
            announcementInteractor.getUserAnnouncements(0)
        } returns Pair(pets, null)
        val viewModel = ProfileViewModel(announcementInteractor)
        viewModel.getAnimalList()
        advanceUntilIdle()
        val state = viewModel.userPetState.value
        assertEquals(ProfileScreenState.Empty, state)
    }


    @Test
    fun `loadUserAnnouncements updates states with error`() = runTest {
        coEvery {
            announcementInteractor.getUserAnnouncements(0)
        } returns Pair(null, -1)
        val viewModel = ProfileViewModel(announcementInteractor)
        viewModel.getAnimalList()
        advanceUntilIdle()
        val state = viewModel.userPetState.value
        assertEquals(ProfileScreenState.Failed, state)
    }


}