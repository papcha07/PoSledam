package apiService.models.search_models

import kotlinx.serialization.Serializable

/**
 * Ответ ручек нейросетевого поиска.
 * GET /api/search/{requestId} -> [SearchResultResponse]
 * GET /api/search             -> List<[SearchResultResponse]>
 */
@Serializable
data class SearchResultResponse(
    val searchImagePath: String? = null,
    val results: List<SimilarAnnouncementResponse>? = null,
    val createdAt: String,
    val errorCode: String? = null
)

@Serializable
data class SimilarAnnouncementResponse(
    val id: String,
    val mainImagePath: String? = null,
    val breed: String? = null,
    val street: String? = null,
    val house: String? = null,
    val district: String? = null,
    /** 0 — находка, 1 — пропажа, 2 — уличное животное */
    val type: Int? = null
)
