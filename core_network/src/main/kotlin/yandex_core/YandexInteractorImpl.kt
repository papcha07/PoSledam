package yandex_core

import model.InternetStatus
import model.geo.AddressSuggestion

class YandexInteractorImpl(
    private val repository: YandexRepository
) : YandexInteractor {

    override suspend fun searchOnce(
        query: String,
        limit: Int,
        lang: String
    ): NetworkResource<List<AddressSuggestion>> {
        val normalized = query.trim().replace(Regex("\\s+"), " ")
        if (normalized.isEmpty()) return NetworkResource.Success(emptyList())
        return repository.search(normalized, limit, lang)
    }

    override suspend fun resolvePointOnceOne(
        lon: Double,
        lat: Double,
        lang: String
    ): NetworkResource<AddressSuggestion> {
        if (lon !in -180.0..180.0 || lat !in -90.0..90.0) {
            return NetworkResource.Failed(InternetStatus.Error)
        }
        return repository.resolveByPointOne(lon, lat, lang)
    }
}
