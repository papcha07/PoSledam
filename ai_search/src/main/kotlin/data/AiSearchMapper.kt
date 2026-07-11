package data

import apiService.models.search_models.SearchResultResponse
import apiService.models.search_models.SimilarAnnouncementResponse
import domain.models.AiSearchResult
import domain.models.SimilarAnnouncement

fun SearchResultResponse.toAiSearchResult(): AiSearchResult {
    return AiSearchResult(
        searchImagePath = searchImagePath,
        results = results.orEmpty().map { it.toSimilarAnnouncement() },
        createdAt = createdAt,
        errorCode = errorCode
    )
}

fun SimilarAnnouncementResponse.toSimilarAnnouncement(): SimilarAnnouncement {
    return SimilarAnnouncement(
        id = id,
        imageUrl = mainImagePath,
        breed = breed,
        street = street,
        house = house,
        district = district,
        type = type
    )
}
