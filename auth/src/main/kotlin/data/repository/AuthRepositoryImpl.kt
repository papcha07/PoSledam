package data.repository

import ApiResponse
import android.util.Log
import apiService.AuthService
import domain.model.LoginInfo
import domain.model.AuthOperationErrorType
import domain.model.AuthOperationResult
import domain.model.LoginErrorType
import domain.model.LoginResult
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


    override suspend fun login(loginInfo: LoginInfo): LoginResult {
        return withContext(Dispatchers.IO) {
            val result = apiService.login(
                loginRequest = LoginRequest(
                    email = loginInfo.email,
                    password = loginInfo.password
                )
            )
            when (result) {
                is ApiResponse.Error -> {
                    result.toLoginResult()
                }

                is ApiResponse.Success<LoginResponse> -> {
                    LoginResult.Success
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

    private fun ApiResponse.Error.toLoginResult(): LoginResult {
        val type = when {
            errorDetails.isInvalidCredentialsError() -> LoginErrorType.InvalidCredentials
            errorDetails.isEmailNotConfirmedError() -> LoginErrorType.EmailNotConfirmed
            errorCode == NO_INTERNET_ERROR_CODE -> LoginErrorType.NoInternet
            errorCode >= SERVER_ERROR_CODE_FROM -> LoginErrorType.Server
            else -> LoginErrorType.Unknown
        }

        return LoginResult.Error(
            type = type,
            message = errorDetails?.message
        )
    }

    private fun ErrorDetails?.isInvalidCredentialsError(): Boolean {
        return matchesBackendError(INVALID_CREDENTIALS_CODE)
    }

    private fun ErrorDetails?.isEmailNotConfirmedError(): Boolean {
        if (matchesBackendError(*EMAIL_NOT_CONFIRMED_CODES)) return true

        val values = backendErrorValues().map { it.lowercase() }
        return values.any { value ->
            (value.contains("email") || value.contains("почт")) &&
                    (value.contains("confirm") ||
                            value.contains("verified") ||
                            value.contains("подтверж"))
        }
    }

    private fun ErrorDetails?.matchesBackendError(vararg expectedCodes: String): Boolean {
        val actualValues = backendErrorValues()
            .map { it.toErrorKey() }
            .filter { it.isNotBlank() }
            .toSet()

        if (actualValues.isEmpty()) return false

        return expectedCodes
            .map { it.toErrorKey() }
            .any { expected ->
                val compactExpected = expected.compactErrorKey()
                actualValues.any { actual ->
                    actual == expected ||
                            actual.compactErrorKey() == compactExpected ||
                            actual.contains(expected) ||
                            actual.compactErrorKey().contains(compactExpected)
                }
            }
    }

    private fun ErrorDetails?.backendErrorValues(): List<String> {
        if (this == null) return emptyList()

        return buildList {
            addIfNotBlank(code)
            addIfNotBlank(message)
            details.orEmpty().forEach { detail ->
                addIfNotBlank(detail.field)
                addIfNotBlank(detail.issue)
                addIfNotBlank(detail.message)
            }
        }
    }

    private fun MutableList<String>.addIfNotBlank(value: String?) {
        if (!value.isNullOrBlank()) add(value)
    }

    private fun String.toErrorKey(): String {
        return replace(Regex("[^A-Za-zА-Яа-я0-9]+"), "_")
            .trim('_')
            .uppercase()
    }

    private fun String.compactErrorKey(): String = replace("_", "")

    private companion object {
        const val NO_INTERNET_ERROR_CODE = -1
        const val SERVER_ERROR_CODE_FROM = 500
        const val INVALID_CREDENTIALS_CODE = "INVALID_CREDENTIALS"
        val EMAIL_NOT_CONFIRMED_CODES = arrayOf(
            "EMAIL_NOT_CONFIRMED",
            "EMAIL_NOT_VERIFIED",
            "EMAIL_CONFIRMATION_REQUIRED",
            "EMAIL_CONFIRMATION_NOT_COMPLETED",
            "NOT_CONFIRMED"
        )
    }

}
