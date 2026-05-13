package di

import data.repository.AnnouncementRepositoryImpl
import domain.notification.NotificationSettingsInteractor
import domain.interactor.AnnouncementInteractor
import domain.interactor.AnnouncementInteractorImpl
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
            mainInteractor = get(),
            notificationSettingsInteractor = get<NotificationSettingsInteractor>()
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