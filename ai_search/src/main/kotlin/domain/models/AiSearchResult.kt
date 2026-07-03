package domain.models

/**
 * Результат одного запроса нейросетевого поиска (UI-модель).
 */
data class AiSearchResult(
    val searchImagePath: String?,
    val results: List<SimilarAnnouncement>,
    /** ISO 8601 дата создания — ключ пагинации истории (lastDateTime). */
    val createdAt: String,
    val errorCode: String?
) {
    val hasError: Boolean get() = errorCode != null
    val isEmpty: Boolean get() = results.isEmpty()
}

/**
 * Похожее объявление, найденное нейросетью.
 *
 * [type]: 0 — находка, 1 — пропажа, 2 — уличное животное.
 * По этому типу открывается соответствующий существующий экран деталей.
 */
data class SimilarAnnouncement(
    val id: String,
    val imageUrl: String?,
    val breed: String?,
    val street: String?,
    val house: String?,
    val district: String?,
    val type: Int?
)
