package ui.di

import android.os.Build
import androidx.annotation.RequiresApi
import data.repository.LocationRepositoryImpl
import data.repository.StreetRepositoryImpl
import domain.interactor.location.LocationInteractor
import domain.interactor.location.LocationInteractorImpl
import domain.interactor.street.StreetPetInteractor
import domain.interactor.street.StreetPetInteractorImpl
import domain.repository.LocationRepository
import domain.repository.StreetRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ui.LocationProvider
import ui.screen.camera.CameraViewModel
import ui.screen.mainScreen.MainScreenViewModel
import ui.screen.street.StreetPetViewModel

@RequiresApi(Build.VERSION_CODES.O)
fun getMainModule() = module {

    single<LocationRepository> {
        LocationRepositoryImpl(
            authService = get()
        )
    }

    single<LocationInteractor> {
        LocationInteractorImpl(
            locationProvider = get(),
            locationRepository = get(),
            userInteractor = get(),
        )
    }

    viewModel {
        MainScreenViewModel(
            notificationInteractor = get(),
            // Background location worker is temporarily disabled for moderation.
            // workerInteractor = get(),
            locationInteractor = get(),
            locationSyncRequestStore = get(),
            streetPetInteractor = get(),
            userInteractor = get()
        )
    }

    single<StreetRepository> {
        StreetRepositoryImpl(get(), get())
    }
    single<StreetPetInteractor> {
        StreetPetInteractorImpl(get())
    }

    viewModel {
        StreetPetViewModel(get(), get())
    }

    viewModel {
        CameraViewModel(get(), get(), get())
    }

    single {
        LocationProvider(androidApplication())
    }
}
