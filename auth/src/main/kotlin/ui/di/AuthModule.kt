package ui.di

import data.repository.AuthRepositoryImpl
import domain.repository.AuthRepository
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ui.login.LoginViewModel
import ui.other.Converter
import ui.register.RegisterViewModel
import usecases.AuthInteractor
import usecases.AuthInteractorImpl

fun getConverter() = module {
    single { Converter(androidContext()) }
}

fun getAuthRepository() = module {
    factory<AuthRepository> {
        AuthRepositoryImpl(
            apiService = get(),
        )
    }
}

fun getAuthInteractor() = module {
    factory<AuthInteractor> {
        AuthInteractorImpl(
            authRepository = get(),
        )
    }
}

fun getAuthViewModel() = module {
    viewModel {
        LoginViewModel(
            authInteractor = get(),
            locationSyncRequestStore = get()
        )
    }

    viewModel {
        RegisterViewModel(
            authInteractor = get()
        )
    }
}
