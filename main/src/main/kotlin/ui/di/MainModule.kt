package ui.di

import data.repository.LocationRepositoryImpl
import data.repository.MainRepositoryImpl
import data.repository.StreetRepositoryImpl
import domain.interactor.LocationInteractor
import domain.interactor.LocationInteractorImpl
import domain.interactor.MainInteractor
import domain.interactor.MainInteractorImpl
import domain.interactor.StreetPetInteractor
import domain.interactor.StreetPetInteractorImpl
import domain.repository.LocationRepository
import domain.repository.MainRepository
import domain.repository.StreetRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ui.camera.CameraViewModel
import ui.mainScreen.MainScreenViewModel
import ui.street.StreetPetViewModel

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
            mainInteractor = get(),
            notificationInteractor = get(),
            locationInteractor = get()
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


