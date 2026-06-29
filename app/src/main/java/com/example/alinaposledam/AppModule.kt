package com.example.alinaposledam

import com.example.alinaposledam.firebase.FirebaseDeviceTokenStore
import com.example.alinaposledam.firebase.FirebaseTokenProvider
import com.example.alinaposledam.firebase.SharedPreferencesFirebaseDeviceTokenStore
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<FirebaseDeviceTokenStore> {
        SharedPreferencesFirebaseDeviceTokenStore(androidContext())
    }

    single {
        FirebaseTokenProvider(
            authService = get(),
            tokenRepository = get(),
            deviceTokenStore = get()
        )
    }

    viewModel {
        ProfileBarViewModel(
            userInteractor = get(),
            yandexInteractor = get(),
            notificationInteractor = get()
        )
    }
}
