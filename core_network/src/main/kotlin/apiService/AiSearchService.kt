package apiService

import ApiResponse
import SendResult
import android.util.Log
import apiService.models.search_models.SearchResultResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import toApiErrorCode
import toSendResultError
import java.io.File

/**
 * Сетевой слой нейросетевого поиска.
 *
 * Использует общий [HttpClient] из core_network (единый baseUrl, авторизация через
 * AuthHeaderPlugin, обработка ошибок). Отдельный клиент здесь НЕ создаётся.
 */
class AiSearchService(private val client: HttpClient) {

    /**
     * POST /api/search/request — создаёт запрос на поиск.
     *
     * multipart/form-data: Image (одно фото), Location.Latitude, Location.Longitude.
     *
     * Ответ приходит с пустым телом (200). Id созданного запроса backend присылает
     * отдельно — через SignalR /hubs/search-announcements либо через FCM push (entity_id).
     */
    suspend fun createSearchRequest(
        image: File,
        latitude: Double,
        longitude: Double
    ): SendResult = withContext(Dispatchers.IO) {
        try {
            val response = client.post("api/search/request") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            appendFilePart(key = "Image", file = image)
                            append("Location.Latitude", latitude.toString())
                            append("Location.Longitude", longitude.toString())
                        }
                    )
                )
            }
            when {
                response.status.isSuccess() -> SendResult.Success
                response.status.value == 400 -> {
                    val text = runCatching { response.bodyAsText() }.getOrNull()
                    SendResult.BadRequest(message = text ?: "Bad request")
                }

                else -> SendResult.BadRequest("HTTP ${response.status.value}")
            }
        } catch (e: Exception) {
            e.toSendResultError(networkMessage = e.message ?: "Unknown error")
        }
    }

    /**
     * GET /api/search/{requestId} — результат конкретного запроса поиска.
     */
    suspend fun getSearchResult(requestId: String): ApiResponse<SearchResultResponse> {
        return try {
            val response = client.get("api/search/$requestId")
            when {
                response.status.isSuccess() -> {
                    val body = response.body<SearchResultResponse>()
                    Log.d("AiSearchService", "result $requestId -> $body")
                    ApiResponse.Success(body)
                }

                else -> ApiResponse.Error(response.status.value)
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.toApiErrorCode())
        }
    }

    /**
     * GET /api/search — история поисков.
     * Пагинация по [lastDateTime] (createdAt последнего полученного результата).
     * Размер страницы фиксирован на backend — 3 результата.
     */
    suspend fun getSearchHistory(lastDateTime: String?): ApiResponse<List<SearchResultResponse>> {
        return try {
            val response = client.get("api/search") {
                url {
                    lastDateTime?.let { parameters.append("lastDateTime", it) }
                }
            }
            when {
                response.status.isSuccess() -> {
                    ApiResponse.Success(response.body<List<SearchResultResponse>>())
                }

                else -> ApiResponse.Error(response.status.value)
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.toApiErrorCode())
        }
    }

    // TODO: Удаление истории поиска. В Swagger DELETE-ручки для /api/search сейчас нет
    //  (доступны только POST /api/search/request, GET /api/search/{requestId}, GET /api/search).
    //  Как только backend добавит DELETE endpoint — реализовать здесь. Фейковый запрос не отправляем.
}
