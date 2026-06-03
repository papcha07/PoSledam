package client

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import storage.TokenRepository

class KtorClient(
    private val tokenRepository: TokenRepository
) {
    fun getInstance(): HttpClient {
        return HttpClient {
            expectSuccess = false

            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }

            install(DefaultRequest) {
                url {
                    host = "10.0.2.2"
                    port = 8080
                    protocol = URLProtocol.HTTP
                    contentType(ContentType.Application.Json)
                    accept(ContentType.Application.Json)
                }

                val token = runBlocking { tokenRepository.getToken() }
                if (token != null) {
                    header("Authorization", "Bearer $token")
                }
            }

            install(HttpTimeout) {
                socketTimeoutMillis = 5000
                connectTimeoutMillis = 5000
                requestTimeoutMillis = 10_000
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("KtorClient", message)
                    }
                }
                level = LogLevel.ALL
            }
        }
    }
}
