package com.example.alinaposledam.viewmodel

import domain.NotificationSettingsInteractor
import domain.interactor.AnnouncementInteractor
import io.mockk.mockk
import kotlinx.coroutines.flow.StateFlow
import org.junit.Before
import org.junit.Test
import ui.viewModel.ActionViewModel
import yandex_core.YandexInteractor
import kotlin.test.assertEquals

class ActionViewModelTest {

    private lateinit var actionViewModel: ActionViewModel
    private val announcementInteractor: AnnouncementInteractor = mockk(relaxed = true)
    private val yandexInteractor: YandexInteractor = mockk(relaxed = true)
    private val notificationSettingsInteractor: NotificationSettingsInteractor =
        mockk(relaxed = true)

    @Before
    fun setUp() {
        actionViewModel = ActionViewModel(
            announcementInteractor = announcementInteractor,
            yandexInteractor = yandexInteractor,
            notificationSettingsInteractor = notificationSettingsInteractor
        )
    }

    @Test
    fun selectLostPetButton() {
        val methodValueFlow: StateFlow<Int> = actionViewModel.methodValueFlow
        actionViewModel.updateMethodValue(0)
        assertEquals(0, methodValueFlow.value)
    }


    @Test
    fun selectFoundPetButton() {
        val methodValueFlow: StateFlow<Int> = actionViewModel.methodValueFlow
        actionViewModel.updateMethodValue(1)
        assertEquals(1, methodValueFlow.value)
    }

    @Test
    fun selectMissButtonAndSelectFoundButton() {
        val methodValueFlow: StateFlow<Int> = actionViewModel.methodValueFlow
        actionViewModel.updateMethodValue(0)
        assertEquals(0, methodValueFlow.value)
        actionViewModel.updateMethodValue(1)
        assertEquals(1, methodValueFlow.value)
    }

    @Test
    fun selectFoundButtonAndSelectMissButton() {
        val methodValueFlow: StateFlow<Int> = actionViewModel.methodValueFlow
        actionViewModel.updateMethodValue(1)
        assertEquals(1, methodValueFlow.value)
        actionViewModel.updateMethodValue(0)
        assertEquals(0, methodValueFlow.value)
    }
}