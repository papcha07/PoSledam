package di

import YandexGeocodeService
import android.content.Context
import apiService.AnnouncementService
import apiService.AuthService
import apiService.StreetService
import client.KtorClient
import client.YandexKtorClient
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import storage.TokenRepository
import storage.UserInfoRepository
import yandex_core.YandexInteractor
import yandex_core.YandexInteractorImpl
import yandex_core.YandexRepository
import yandex_core.YandexRepositoryImpl

val ktorClientModule = module {
    single { KtorClient(get()) }

    single { get<KtorClient>().getInstance() }
}

fun getCoreNetworkModule() = module {
    single {
        AuthService(
            client = get(),
            tokenRepository = get(),
        )
    }
}

fun getAnnouncementService() = module {
    single {
        AnnouncementService(
            client = get() // тоже из Koin
        )
    }
}

fun getStreetService() = module {
    single {
        StreetService(
            get()
        )
    }
}

fun getYandexSuggestService() = module {
    single {
        YandexGeocodeService(
            client = YandexKtorClient.getInstance(),
            apiKey = "17021327-d1ff-4d2d-9559-8ce95c2d55af"
        )
    }
}


val tokenRepositoryModule = module {
    single {
        androidContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }
    single<TokenRepository> { TokenRepository.Base(get()) }
}

val userInfoRepository = module {
    single<UserInfoRepository> {
        UserInfoRepository.Base(
            sharedPreferences = get(),
            json = Json,
        )
    }
}

fun getYandexRepository() = module {
    factory<YandexRepository> {
        YandexRepositoryImpl(
            service = get()
        )
    }
}

fun getYandexInteractor() = module {
    factory<YandexInteractor> {
        YandexInteractorImpl(
            repository = get()
        )
    }
}

