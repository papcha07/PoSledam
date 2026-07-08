package di

import GeoapifyGeocodeService
import android.content.Context
import apiService.AiSearchService
import apiService.AnnouncementService
import apiService.AuthService
import apiService.PetMarketService
import apiService.StreetService
import client.GeoapifyKtorClient
import client.KtorClient
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import storage.TokenRepository
import storage.UserInfoRepository
import yandex_core.GeoapifyRepositoryImpl
import yandex_core.YandexInteractor
import yandex_core.YandexInteractorImpl
import yandex_core.YandexRepository

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

fun getAiSearchService() = module {
    single {
        AiSearchService(
            client = get() // общий HttpClient из ktorClientModule
        )
    }
}

fun getPetMarketService() = module {
    single {
        PetMarketService(
            client = get()
        )
    }
}

// Геокодер переведён с Yandex на Geoapify. Реализация Yandex оставлена в проекте
// (client/YandexKtorClient, geoCoderService/YandexGeocodeService, yandex_core/YandexRepositoryImpl),
// но больше не подключается через DI. Имена DI-функций сохранены, чтобы не трогать App.kt
// и feature-модули — контракт YandexInteractor/YandexRepository/AddressSuggestion не изменился.
private const val GEOAPIFY_API_KEY = "22a41b300478445bba4e186c8f29f4b1"

fun getYandexSuggestService() = module {
    single {
        GeoapifyGeocodeService(
            client = GeoapifyKtorClient.getInstance(),
            apiKey = GEOAPIFY_API_KEY
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
        GeoapifyRepositoryImpl(
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
