package com.example.alinaposledam.viewmodel

import domain.notification.NotificationSettingsInteractor
import domain.interactor.AnnouncementInteractor
import io.mockk.mockk
import ui.viewModel.ActionViewModel
import yandex_core.YandexInteractor

class ActionViewModelTest {

    private lateinit var actionViewModel: ActionViewModel
    private val announcementInteractor: AnnouncementInteractor = mockk(relaxed = true)
    private val yandexInteractor: YandexInteractor = mockk(relaxed = true)
    private val notificationSettingsInteractor: NotificationSettingsInteractor =
        mockk(relaxed = true)

}