package yandex_core

import GeoapifyGeocodeService
import ResultWrapper
import model.InternetStatus
import model.geo.AddressSuggestion

/**
 * Реализация геокодера через Geoapify за существующим контрактом [YandexRepository].
 * Feature-модули продолжают работать без изменений (тот же интерфейс и [NetworkResource]).
 */
class GeoapifyRepositoryImpl(
    private val service: GeoapifyGeocodeService
) : YandexRepository {

    override suspend fun search(
        query: String,
        limit: Int,
        lang: String
    ): NetworkResource<List<AddressSuggestion>> {
        return when (val result = service.geocode(query, lang)) {
            is ResultWrapper.Success<List<AddressSuggestion>> ->
                NetworkResource.Success(result.data.take(limit))

            is ResultWrapper.NetworkError -> NetworkResource.Failed(InternetStatus.NoInternet)
            is ResultWrapper.HttpError,
            is ResultWrapper.UnknownError -> NetworkResource.Failed(InternetStatus.Error)
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
