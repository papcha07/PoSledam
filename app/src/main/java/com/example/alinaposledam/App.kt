package com.example.alinaposledam

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import com.example.alinaposledam.worker.location_worker.factory.KoinWorkerFactory
import com.google.firebase.FirebaseApp
import dataStoreModule
import di.getActionViewModel
import di.getAnnouncementInteractor
import di.getAnnouncementRepository
import di.getAnnouncementService
import di.getCoreNetworkModule
import di.getFilterViewModel
import di.getImageLoaderModule
import di.getProfileSettingsViewModel
import di.getSearchInteractor
import di.getSearchRepository
import di.getStreetService
import di.getYandexInteractor
import di.getYandexRepository
import di.getYandexSuggestService
import di.ktorClientModule
import di.tokenRepositoryModule
import di.userInfoRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ui.coreDi
import ui.di.getAuthInteractor
import ui.di.getAuthRepository
import ui.di.getAuthViewModel
import ui.di.getConverter
import ui.di.getMainModule

class App : Application(), Configuration.Provider {
    override fun onCreate() {
        super.onCreate()

        com.yandex.mapkit.MapKitFactory.setApiKey("2c2d848e-690c-467f-80df-df3ad423160d")
        com.yandex.mapkit.MapKitFactory.initialize(this)
        val app = FirebaseApp.initializeApp(this)
        Log.d("FCM_CHECK", "FirebaseApp = $app")

        startKoin {
            androidContext(this@App)
            modules(
                listOf(
                    getCoreNetworkModule(),
                    getAuthInteractor(),
                    getAuthRepository(),
                    getAuthViewModel(),
                    getConverter(),

                    getAnnouncementService(),
                    getAnnouncementRepository(),
                    getAnnouncementInteractor(),
                    getActionViewModel(),
                    getProfileSettingsViewModel(),

                    getYandexSuggestService(),
                    getYandexRepository(),
                    getYandexInteractor(),
                    tokenRepositoryModule,
                    ktorClientModule,
                    getFilterViewModel(),
                    getSearchRepository(),
                    getSearchInteractor(),
                    getMainModule(),
                    getProfileSettingsViewModel(),
                    dataStoreModule,
                    userInfoRepository,
                    getStreetService(),
                    coreDi,
                    getWorkerModule(),
                    getImageLoaderModule()
                )
            )
        }


    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(KoinWorkerFactory())
            .build()
}