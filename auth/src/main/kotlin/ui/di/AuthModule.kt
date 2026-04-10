package ui.di

import data.repository.AuthRepositoryImpl
import domain.repository.AuthRepository
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ui.AuthViewModel
import ui.other.Converter
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
            tokenRepository = get()
        )
    }
}

fun getAuthViewModel() = module {
    viewModel {
        AuthViewModel(
            authInteractor = get()
        )
    }
}