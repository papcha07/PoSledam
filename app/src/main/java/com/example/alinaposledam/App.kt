package com.example.alinaposledam

import android.app.Application
import dataStoreModule
import di.getActionViewModel
import di.getAiSearchInteractor
import di.getAiSearchRepository
import di.getAiSearchService
import di.getAiSearchViewModel
import di.getAnnouncementInteractor
import di.getAnnouncementRepository
import di.getAnnouncementService
import di.getCoreNetworkModule
import di.getFilterViewModel
import di.getImageLoaderModule
import di.getMarketInteractor
import di.getMarketRepository
import di.getMarketViewModel
import di.getPetMarketService
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
// import androidx.work.Configuration
// import worker.location_worker.factory.KoinWorkerFactory
// import worker.location_worker.getWorkerModule

class App : Application() {
    override fun onCreate() {
        super.onCreate()

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
                    dataStoreModule,
                    userInfoRepository,
                    getStreetService(),
                    getAiSearchService(),
                    getPetMarketService(),
                    getAiSearchRepository(),
                    getAiSearchInteractor(),
                    getAiSearchViewModel(),
                    getMarketRepository(),
                    getMarketInteractor(),
                    getMarketViewModel(),
                    coreDi,
                    // Background location worker is temporarily disabled for moderation.
                    // getWorkerModule(),
                    getImageLoaderModule(),
                    appModule
                )
            )
        }


    }

    // Background location worker is temporarily disabled for moderation.
    // override val workManagerConfiguration: Configuration
    //     get() = Configuration.Builder()
    //         .setWorkerFactory(KoinWorkerFactory())
    //         .build()
}
