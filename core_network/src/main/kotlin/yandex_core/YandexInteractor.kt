package yandex_core

import model.geo.AddressSuggestion

interface YandexInteractor {
    suspend fun searchOnce(
        query: String,
        limit: Int = 10,
        lang: String = "ru_RU"
    ): NetworkResource<List<AddressSuggestion>>

    suspend fun resolvePointOnceOne(
        lon: Double,
        lat: Double,
        lang: String = "ru_RU"
    ): NetworkResource<AddressSuggestion>
}