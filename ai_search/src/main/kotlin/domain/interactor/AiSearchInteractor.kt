package domain.interactor

import android.net.Uri
import androidx.paging.PagingData
import domain.models.AiSearchResult
import kotlinx.coroutines.flow.Flow
import model.InternetStatus
import ui.model.Response

interface AiSearchInteractor {

    suspend fun createSearchRequest(imageUri: Uri, latitude: Double, longitude: Double): Response
    suspend fun getSearchResult(requestId: String): Pair<AiSearchResult?, InternetStatus?>
    fun loadSearchHistory(): Flow<PagingData<AiSearchResult>>
}
