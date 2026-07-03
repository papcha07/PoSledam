package data

import ApiResponse
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import apiService.AiSearchService
import domain.models.AiSearchResult

/**
 * Пагинация истории поисков GET /api/search.
 * Ключ страницы — lastDateTime (createdAt последнего полученного результата).
 * Размер страницы фиксирован на backend — [PAGE_SIZE] = 3.
 */
class AiSearchHistoryPagingSource(
    private val service: AiSearchService,
) : PagingSource<String, AiSearchResult>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, AiSearchResult> {
        return try {
            val lastDateTime = params.key
            when (val response = service.getSearchHistory(lastDateTime)) {
                is ApiResponse.Success -> {
                    val items = response.data.map { it.toAiSearchResult() }
                    val nextKey =
                        if (items.size < PAGE_SIZE) null else items.lastOrNull()?.createdAt
                    LoadResult.Page(
                        data = items,
                        prevKey = null,
                        nextKey = nextKey
                    )
                }

                is ApiResponse.Error -> {
                    Log.d("AiSearchHistory", "error ${response.errorCode}")
                    LoadResult.Error(Exception("Ошибка загрузки истории поиска"))
                }
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, AiSearchResult>): String? = null

    internal companion object {
        const val PAGE_SIZE = 3
    }
}
