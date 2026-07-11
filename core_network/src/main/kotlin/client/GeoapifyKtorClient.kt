package client

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Отдельный клиент для внешнего геокодера Geoapify (api.geoapify.com).
 * Отдельный host, поэтому не переиспользуем основной авторизованный KtorClient —
 * ровно как это было сделано для Yandex ([YandexKtorClient]).
 */
object GeoapifyKtorClient {

    fun getInstance(): HttpClient {
        return HttpClient {
            // Пусть 4xx/5xx бросают исключения — safeCall корректно замапит их в HttpError.
            expectSuccess = true

            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    }
                )
            }

            install(DefaultRequest) {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "api.geoapify.com"
                }
                headers.append("Accept", ContentType.Application.Json.toString())
            }

            install(HttpTimeout) {
                socketTimeoutMillis = 10_000
                connectTimeoutMillis = 10_000
                requestTimeoutMillis = 10_000
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("GeoapifyKtorClient", message)
                    }
                }
                level = LogLevel.ALL
            }
        }
    }
}
