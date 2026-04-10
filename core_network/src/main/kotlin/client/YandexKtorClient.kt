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


object YandexKtorClient {

    fun getInstance(): HttpClient {
        return HttpClient {
            expectSuccess = false

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
                    host = "geocode-maps.yandex.ru"
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
                        Log.d("YandexKtorClient", message)
                    }
                }
                level = LogLevel.ALL
            }


        }
    }
}

