package apiService

import ApiResponse
import SendResult
import android.util.Log
import apiService.models.auth_models.DeviceTokenRequest
import apiService.models.auth_models.LocationRequestDto
import apiService.models.auth_models.UpdateUserInfoRequest
import apiService.models.auth_models.UserInfoResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import model.auth.request.LoginRequest
import model.auth.request.RegisterRequest
import model.auth.request.ResendEmailConfirmationRequest
import model.auth.response.LoginResponse
import model.errorResponse.ErrorDetails
import model.errorResponse.ErrorResponse
import storage.TokenRepository
import toApiErrorCode
import toSendResultError
import java.io.File

class AuthService(
    private val client: HttpClient,
    private val tokenRepository: TokenRepository
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun register(registerRequest: RegisterRequest): ApiResponse<Unit> {
        return try {
            val response = client.submitFormWithBinaryData(
                url = "api/auth/register",
                formData = formData {
                    append("Email", registerRequest.email)
                    append("Password", registerRequest.password)
                    append("FirstName", registerRequest.firstName)
                    registerRequest.description?.takeIf { it.isNotBlank() }?.let {
                        append("Description", it)
                    }
                    registerRequest.contacts?.forEachIndexed { index, contact ->
                        append("UserContacts[$index].contactType", contact.contactType.toString())
                        append("UserContacts[$index].url", contact.url)
                    }
                }
            )
            if (response.status.isSuccess()) {
                ApiResponse.Success(Unit)
            } else {
                val errorDetails = response.errorDetails()
                Log.d("errorRegisterResponse", errorDetails.toString())
                when (errorDetails?.details?.firstOrNull()?.issue) {
                    "NOT_UNIQUE" -> {
                        ApiResponse.Error(403, errorDetails)
                    }

                    else -> {
                        ApiResponse.Error(response.status.value, errorDetails)
                    }
                }
            }

        } catch (e: Exception) {
            Log.d("RegisterViewModel", e.message.toString())
            ApiResponse.Error(e.toApiErrorCode())
        }
    }


    suspend fun login(loginRequest: LoginRequest): ApiResponse<LoginResponse> {
        return try {
            val response = client.post("api/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(loginRequest)
            }
            if (response.status.isSuccess()) {
                val body = response.body<LoginResponse>()
                tokenRepository.saveToken(body.token)
                ApiResponse.Success(body)
            } else {
                ApiResponse.Error(response.status.value, response.errorDetails())
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.toApiErrorCode())
        }
    }

    suspend fun resendEmailConfirmation(
        request: ResendEmailConfirmationRequest
    ): ApiResponse<Unit> {
        return try {
            val response = client.post("api/auth/resend-email-confirmation") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            if (response.status.isSuccess()) {
                ApiResponse.Success(Unit)
            } else {
                ApiResponse.Error(response.status.value, response.errorDetails())
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.toApiErrorCode())
        }
    }

    suspend fun getInfo(): ApiResponse<UserInfoResponse> {
        return try {
            val response = client.get("api/auth/me")
            if (response.status.isSuccess()) {
                ApiResponse.Success(response.body<UserInfoResponse>())
            } else {
                ApiResponse.Error(response.status.value)
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.toApiErrorCode())
        }
    }

    suspend fun updateUserInfo(updateRequest: UpdateUserInfoRequest): Boolean {
        return try {
            val request = client.put("api/user/${updateRequest.id}/userInfo") {
                setBody(updateRequest)
            }
            if (request.status.isSuccess()) true else false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateUserImage(file: File, id: String) {
        try {
            val response = client.submitFormWithBinaryData(
                url = "api/user/$id/avatar",
                formData = formData {
                    appendFilePart(key = "AvatarImage", file = file)
                }
            ) {
                method = HttpMethod.Put
            }
            Log.d("updateUserImage", "code=${response.status}")
        } catch (e: Exception) {
            Log.d("updateUserImage", e.message.toString())
        }
    }


    suspend fun sendDeviceToken(deviceToken: DeviceTokenRequest) {
        client.post("api/user/device") {
            setBody(deviceToken)
        }
    }

    suspend fun sendCurrentLocation(locationRequest: LocationRequestDto): SendResult {
        return withContext(Dispatchers.IO) {
            try {
                val response = client.put("api/user/location") {
                    contentType(ContentType.Application.Json)
                    setBody(locationRequest)
                }

                if (response.status.isSuccess()) {
                    SendResult.Success
                } else {
                    SendResult.BadRequest("Location update failed with code ${response.status.value}")
                }
            } catch (e: Exception) {
                e.toSendResultError(
                    networkMessage = e.message ?: "Location update network error"
                )
            }
        }
    }

    private suspend fun HttpResponse.errorDetails(): ErrorDetails? {
        val text = bodyAsText()
        if (text.isBlank()) return null

        return runCatching {
            json.decodeFromString<ErrorResponse>(text).asErrorDetails()
        }.getOrNull() ?: runCatching {
            json.decodeFromString<ErrorDetails>(text)
        }.getOrNull()
    }
}
