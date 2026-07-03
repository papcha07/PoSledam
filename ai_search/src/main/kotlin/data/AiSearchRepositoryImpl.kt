package data

import ApiResponse
import SendResult
import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import apiService.AiSearchService
import data.AiSearchHistoryPagingSource.Companion.PAGE_SIZE
import domain.models.AiSearchResult
import domain.repository.AiSearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import model.InternetStatus
import ui.model.Response
import ui.other.Converter

class AiSearchRepositoryImpl(
    private val service: AiSearchService,
    private val converter: Converter
) : AiSearchRepository {

    override suspend fun createSearchRequest(
        imageUri: Uri,
        latitude: Double,
        longitude: Double
    ): Response = withContext(Dispatchers.IO) {
        // Переиспользуем существующую утилиту чтения Uri -> File (как в отправке фото объявлений).
        val file = converter.convertToFile(imageUri.toString())
        val result = service.createSearchRequest(
            image = file,
            latitude = latitude,
            longitude = longitude
        )
        when (result) {
            is SendResult.BadRequest -> Response.SERVER_ERROR
            is SendResult.Error -> Response.INTERNET_ERROR
            SendResult.Success -> Response.SUCCESS
        }
    }

    override suspend fun getSearchResult(
        requestId: String
    ): Pair<AiSearchResult?, InternetStatus?> = withContext(Dispatchers.IO) {
        when (val response = service.getSearchResult(requestId)) {
            is ApiResponse.Success -> Pair(response.data.toAiSearchResult(), null)
            is ApiResponse.Error -> when (response.errorCode) {
                -1 -> Pair(null, InternetStatus.NoInternet)
                else -> Pair(null, InternetStatus.Error)
            }
        }
    }

    override fun loadSearchHistory(): Flow<PagingData<AiSearchResult>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                AiSearchHistoryPagingSource(service = service)
            }
        ).flow
    }
}
