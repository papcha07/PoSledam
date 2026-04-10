import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.HttpRequest
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText

suspend inline fun <reified T> safeCall(
    call: suspend () -> HttpResponse
): ResultWrapper<T> {
    return try {
        val response = call()
        val body: T = response.body()
        ResultWrapper.Success(body)
    } catch (e: ClientRequestException) { // 4xx
        val errorBody = e.response.bodyAsText()
        ResultWrapper.HttpError(e.response.status.value, errorBody)
    } catch (e: ServerResponseException) { // 5xx
        val errorBody = e.response.bodyAsText()
        ResultWrapper.HttpError(e.response.status.value, errorBody)
    } catch (e: ResponseException) {
        val errorBody = e.response.bodyAsText()
        ResultWrapper.HttpError(e.response.status.value, errorBody)
    } catch (e: io.ktor.utils.io.errors.IOException) {
        ResultWrapper.NetworkError(e)
    } catch (e: Exception) {
        ResultWrapper.UnknownError(e)
    }
}

sealed class ResultWrapper<out T> {
    data class Success<T>(val data: T) : ResultWrapper<T>()
    data class HttpError(val code: Int, val body: String) : ResultWrapper<Nothing>()
    data class NetworkError(val exception: Throwable) : ResultWrapper<Nothing>()
    data class UnknownError(val exception: Throwable) : ResultWrapper<Nothing>()
}
