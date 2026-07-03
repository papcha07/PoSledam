package domain.interactor

import android.net.Uri
import androidx.paging.PagingData
import domain.models.AiSearchResult
import domain.repository.AiSearchRepository
import kotlinx.coroutines.flow.Flow
import model.InternetStatus
import ui.model.Response

class AiSearchInteractorImpl(
    private val repository: AiSearchRepository
) : AiSearchInteractor {

    override suspend fun createSearchRequest(
        imageUri: Uri,
        latitude: Double,
        longitude: Double
    ): Response = repository.createSearchRequest(imageUri, latitude, longitude)

    override suspend fun getSearchResult(
        requestId: String
    ): Pair<AiSearchResult?, InternetStatus?> = repository.getSearchResult(requestId)

    override fun loadSearchHistory(): Flow<PagingData<AiSearchResult>> =
        repository.loadSearchHistory()
}
