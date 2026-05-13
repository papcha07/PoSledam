package ui.di

import data.repository.LocationRepositoryImpl
import data.repository.MainRepositoryImpl
import data.repository.StreetRepositoryImpl
import domain.interactor.location.LocationInteractor
import domain.interactor.location.LocationInteractorImpl
import domain.interactor.main.MainInteractor
import domain.interactor.main.MainInteractorImpl
import domain.interactor.street.StreetPetInteractor
import domain.interactor.street.StreetPetInteractorImpl
import domain.repository.LocationRepository
import domain.repository.MainRepository
import domain.repository.StreetRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ui.screen.camera.CameraViewModel
import ui.screen.mainScreen.MainScreenViewModel
import ui.screen.street.StreetPetViewModel

fun getMainModule() = module {

    val mainRepository = single<MainRepository> {
        MainRepositoryImpl(
            authService = get(),
            userInfoRepository = get()
        )
    }
    val mainInteractor =
        single<MainInteractor> {
            MainInteractorImpl(get(), get())
        }

    single<LocationRepository> {
        LocationRepositoryImpl(
            authService = get()
        )
    }

    single<LocationInteractor> {
        LocationInteractorImpl(
            locationProvider = get(),
            locationRepository = get()
        )
    }

    viewModel {
        MainScreenViewModel(
            notificationInteractor = get(),
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
        StreetPetViewModel(get())
    }

    viewModel {
        CameraViewModel(get(), get(), get())
    }

    single {
        domain.LocationProvider(androidApplication())
    }
}


