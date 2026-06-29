package client

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
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
                    host = "161.104.52.29"
                    port = 8080
                    protocol = URLProtocol.HTTP
                    contentType(ContentType.Application.Json)
                    accept(ContentType.Application.Json)
                }

            }

            install(AuthHeaderPlugin) {
                tokenProvider = tokenRepository::getToken
            }

            install(HttpTimeout) {
                socketTimeoutMillis = 15_000
                connectTimeoutMillis = 15_000
                requestTimeoutMillis = 15_000
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

private class AuthHeaderPluginConfig {
    var tokenProvider: suspend () -> String? = { null }
}

private val AuthHeaderPlugin = createClientPlugin(
    name = "AuthHeaderPlugin",
    createConfiguration = ::AuthHeaderPluginConfig
) {
    val tokenProvider = pluginConfig.tokenProvider

    onRequest { request, _ ->
        val token = tokenProvider()
        if (!token.isNullOrBlank() && !request.headers.contains(HttpHeaders.Authorization)) {
            request.headers.append(HttpHeaders.Authorization, "Bearer $token")
        }
    }
}
