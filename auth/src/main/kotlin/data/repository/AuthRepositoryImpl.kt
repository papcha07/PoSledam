package data.repository

import ApiResponse
import android.util.Log
import apiService.AuthService
import domain.model.LoginInfo
import domain.model.AuthOperationErrorType
import domain.model.AuthOperationResult
import domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.auth.request.LoginRequest
import model.auth.request.RegisterRequest
import model.auth.request.ResendEmailConfirmationRequest
import model.auth.request.SocialMedia
import model.auth.response.LoginResponse
import model.errorResponse.ErrorDetails
import ui.model.data.UserDataInfo

class AuthRepositoryImpl(
    private val apiService: AuthService,
) : AuthRepository {

    override suspend fun register(registerInfo: UserDataInfo): Pair<Boolean, Int?> {
        return withContext(Dispatchers.IO) {
            val resultOfResponse = apiService.register(
                registerRequest = RegisterRequest(
                    email = registerInfo.email,
                    password = registerInfo.password,
                    firstName = registerInfo.name,
                    description = registerInfo.description,
                    contacts = registerInfo.contacts.map { contact ->
                        SocialMedia(
                            contactType = contact.contactType ?: 0,
                            url = contact.url
                        )
                    }
                )
            )
            Log.d("RegisterViewModel", resultOfResponse.toString())


            when (resultOfResponse) {
                is ApiResponse.Error -> Pair(false, resultOfResponse.errorCode)
                is ApiResponse.Success<Unit> -> Pair(true, null)
            }
        }
    }


    override suspend fun login(loginInfo: LoginInfo): Pair<Boolean, Int?> {
        return withContext(Dispatchers.IO) {
            val result = apiService.login(
                loginRequest = LoginRequest(
                    email = loginInfo.email,
                    password = loginInfo.password
                )
            )
            when (result) {
                is ApiResponse.Error -> {
                    Pair(false, result.errorCode)
                }

                is ApiResponse.Success<LoginResponse> -> {
                    Pair(true, null)
                }
            }
        }
    }

    override suspend fun resendEmailConfirmation(email: String): AuthOperationResult {
        return withContext(Dispatchers.IO) {
            val result = apiService.resendEmailConfirmation(
                ResendEmailConfirmationRequest(email = email)
            )

            when (result) {
                is ApiResponse.Success -> AuthOperationResult.Success
                is ApiResponse.Error -> {
                    AuthOperationResult.Error(
                        type = when {
                            result.errorDetails.isEmailConfirmationCooldownError() ->
                                AuthOperationErrorType.TooManyEmailConfirmationRequests

                            result.errorCode == NO_INTERNET_ERROR_CODE ->
                                AuthOperationErrorType.NoInternet

                            result.errorCode == 404 ->
                                AuthOperationErrorType.NotFound

                            else -> AuthOperationErrorType.Unknown
                        }
                    )
                }
            }
        }
    }

    private fun ErrorDetails?.isEmailConfirmationCooldownError(): Boolean {
        val detail = this?.details?.firstOrNull() ?: return false
        return code == "DOMAIN_RULE_VIOLATION" &&
                detail.field == "EmailConfirmationLastSentAt" &&
                detail.issue == "TOO_MANY"
    }

    private companion object {
        const val NO_INTERNET_ERROR_CODE = -1
    }

}
