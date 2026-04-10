package yandex_core

import ResultWrapper
import YandexGeocodeService
import model.InternetStatus
import model.geo.AddressSuggestion

sealed class NetworkResource<T> {
    data class Success<T>(val data: T) : NetworkResource<T>()
    data class Failed<T>(val type: InternetStatus) : NetworkResource<T>()
}

class YandexRepositoryImpl(
    private val service: YandexGeocodeService
) : YandexRepository {

    override suspend fun search(
        query: String,
        limit: Int,
        lang: String
    ): NetworkResource<List<AddressSuggestion>> {
        return when (val result = service.geocode(query, lang)) {
            is ResultWrapper.Success<List<AddressSuggestion>> -> {
                NetworkResource.Success(result.data)
            }

            is ResultWrapper.HttpError -> {
                NetworkResource.Failed(InternetStatus.Error)
            }

            is ResultWrapper.NetworkError -> {
                NetworkResource.Failed(InternetStatus.NoInternet)
            }

            is ResultWrapper.UnknownError -> {
                NetworkResource.Failed(InternetStatus.Error)
            }
        }
    }

    override suspend fun resolveByPointOne(
        lon: Double,
        lat: Double,
        lang: String
    ): NetworkResource<AddressSuggestion> {
        return when (val result = service.reverseGeocodeOne(lon, lat, lang)) {
            is ResultWrapper.Success<AddressSuggestion> -> NetworkResource.Success(result.data)
            is ResultWrapper.NetworkError -> NetworkResource.Failed(InternetStatus.NoInternet)
            is ResultWrapper.HttpError,
            is ResultWrapper.UnknownError -> NetworkResource.Failed(InternetStatus.Error)
        }
    }
}
