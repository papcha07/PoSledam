package yandex_core

import model.geo.AddressSuggestion

// domain/YandexRepository.kt
interface YandexRepository {
    suspend fun search(
        query: String,
        limit: Int = 10,
        lang: String = "ru_RU"
    ): NetworkResource<List<AddressSuggestion>>

    suspend fun resolveByPointOne(
        lon: Double,
        lat: Double,
        lang: String = "ru_RU"
    ): NetworkResource<AddressSuggestion>
}

