package di

import data.repository.AnnouncementRepositoryImpl
import domain.interactor.announcement.AnnouncementInteractor
import domain.interactor.announcement.AnnouncementInteractorImpl
import domain.notification.NotificationSettingsInteractor
import domain.repository.AnnouncementRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ui.viewModel.ActionViewModel
import ui.viewModel.ProfileSettingsViewModel
import ui.viewModel.ProfileViewModel

fun getAnnouncementRepository() = module {
    factory<AnnouncementRepository> {
        AnnouncementRepositoryImpl(
            apiService = get(),
            converter = get()
        )
    }
}

fun getAnnouncementInteractor() = module {
    factory<AnnouncementInteractor> {
        AnnouncementInteractorImpl(
            announcementRepository = get()
        )
    }
}

fun getProfileSettingsViewModel() = module {
    viewModel {
        ProfileSettingsViewModel(
            notificationSettingsInteractor = get<NotificationSettingsInteractor>(),
            userInteractor = get()
        )
    }
}


fun getActionViewModel() = module {
    viewModel {
        ActionViewModel(
            announcementInteractor = get(),
            yandexInteractor = get(),
            notificationSettingsInteractor = get()
        )
    }

    viewModel {
        ProfileViewModel(get())
    }
}