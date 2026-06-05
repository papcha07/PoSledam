package di

import data.repository.AnnouncementRepositoryImpl
import data.repository.ImageLoaderRepositoryImpl
import domain.interactor.announcement.AnnouncementInteractor
import domain.interactor.announcement.AnnouncementInteractorImpl
import domain.interactor.loader.ImageLoaderInteractor
import domain.interactor.loader.ImageLoaderInteractorImpl
import domain.notification.NotificationSettingsInteractor
import domain.repository.AnnouncementRepository
import domain.repository.ImageLoaderRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ui.viewModel.ActionViewModel
import ui.viewModel.ProfileAnnouncementDetailsViewModel
import ui.viewModel.ProfileSettingsViewModel
import ui.viewModel.ProfileViewModel

fun getImageLoaderModule() = module {
    single<ImageLoaderRepository> {
        ImageLoaderRepositoryImpl(
            authService = get()
        )
    }

    single<ImageLoaderInteractor> {
        ImageLoaderInteractorImpl(
            loaderRepository = get(),
            converter = get()
        )
    }
}

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
            userInteractor = get(),
            imageLoaderInteractor = get()
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

    viewModel {
        ProfileAnnouncementDetailsViewModel(get())
    }
}
