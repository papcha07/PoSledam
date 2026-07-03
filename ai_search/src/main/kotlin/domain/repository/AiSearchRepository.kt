package domain.repository

import android.net.Uri
import androidx.paging.PagingData
import domain.models.AiSearchResult
import kotlinx.coroutines.flow.Flow
import model.InternetStatus
import ui.model.Response

interface AiSearchRepository {

    /**
     * Создать запрос на поиск по одному фото и координатам пользователя.
     * POST /api/search/request (multipart).
     */
    suspend fun createSearchRequest(imageUri: Uri, latitude: Double, longitude: Double): Response

    /**
     * Получить результат конкретного запроса.
     * GET /api/search/{requestId}.
     */
    suspend fun getSearchResult(requestId: String): Pair<AiSearchResult?, InternetStatus?>

    /**
     * История поисков через Paging 3 (пагинация по lastDateTime, страница = 3).
     * GET /api/search.
     */
    fun loadSearchHistory(): Flow<PagingData<AiSearchResult>>
}
