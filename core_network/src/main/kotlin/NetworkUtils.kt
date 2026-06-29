import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.HttpRequest
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CancellationException
import java.io.IOException
import java.nio.channels.UnresolvedAddressException

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
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        if (e.isNetworkException()) {
            ResultWrapper.NetworkError(e)
        } else {
            ResultWrapper.UnknownError(e)
        }
    }
}

fun Throwable.isNetworkException(): Boolean {
    var current: Throwable? = this
    while (current != null) {
        if (
            current is IOException ||
            current is UnresolvedAddressException
        ) {
            return true
        }
        current = current.cause
    }
    return false
}

fun Throwable.toApiErrorCode(defaultErrorCode: Int = 400): Int {
    if (this is CancellationException) throw this
    return if (isNetworkException()) NO_INTERNET_ERROR_CODE else defaultErrorCode
}

fun Throwable.toSendResultError(
    networkMessage: String = message ?: "Проблемы с соединением",
    badRequestMessage: String = "Bad request"
): SendResult {
    if (this is CancellationException) throw this
    return if (isNetworkException()) {
        SendResult.Error(networkMessage)
    } else {
        SendResult.BadRequest(badRequestMessage)
    }
}

sealed class ResultWrapper<out T> {
    data class Success<T>(val data: T) : ResultWrapper<T>()
    data class HttpError(val code: Int, val body: String) : ResultWrapper<Nothing>()
    data class NetworkError(val exception: Throwable) : ResultWrapper<Nothing>()
    data class UnknownError(val exception: Throwable) : ResultWrapper<Nothing>()
}

private const val NO_INTERNET_ERROR_CODE = -1
