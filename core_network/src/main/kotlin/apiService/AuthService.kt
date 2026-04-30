package apiService

import ApiResponse
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
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.auth.request.LoginRequest
import model.auth.request.RegisterRequest
import model.auth.response.LoginResponse
import model.errorResponse.ErrorResponse
import storage.TokenRepository
import java.io.File

class AuthService(
    private val client: HttpClient,
    private val tokenRepository: TokenRepository
) {

    suspend fun register(registerRequest: RegisterRequest): ApiResponse<Unit> {
        return try {
            val response = client.submitFormWithBinaryData(
                url = "api/auth/register",
                formData = formData {
                    append("email", registerRequest.email)
                    append("password", registerRequest.password)
                    append("firstName", registerRequest.firstName)
                    append("description", registerRequest.description ?: "")
                    registerRequest.contacts?.forEachIndexed { index, contact ->
                        append("UserContacts[$index].contactType", contact.contactType.toString())
                        append("UserContacts[$index].url", contact.url)
                    }
                }
            )
            if (response.status.isSuccess()) {
                ApiResponse.Success(Unit)
            } else {
                val response = response.body<ErrorResponse>()
                Log.d("errorRegisterResponse", response.toString())
                when (response.error.details[0].issue) {
                    "NOT_UNIQUE" -> {
                        ApiResponse.Error(403)
                    }

                    else -> {
                        ApiResponse.Error(400)
                    }
                }
            }

        } catch (e: Exception) {
            Log.d("RegisterViewModel", e.message.toString())
            ApiResponse.Error(-1)
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
                ApiResponse.Error(400)
            }
        } catch (e: Exception) {
            ApiResponse.Error(-1)
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
            ApiResponse.Error(-1)
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
                    append(
                        "AvatarImage",
                        file.readBytes(),
                        Headers.build {
                            append(
                                HttpHeaders.ContentDisposition,
                                "form-data; name=\"AvatarImage\"; filename=\"${file.name}\""
                            )
                            append(
                                HttpHeaders.ContentType,
                                ContentType.Image.JPEG.toString() // или PNG, если нужно
                            )
                        }
                    )
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

    suspend fun sendCurrentLocation(locationRequest: LocationRequestDto) {
        withContext(Dispatchers.IO) {
            client.put("api/user/location") {
                setBody(locationRequest)
            }
        }
    }

}