package data.repository

import ApiResponse
import android.util.Log
import apiService.AuthService
import domain.model.LoginInfo
import domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.auth.request.LoginRequest
import model.auth.request.RegisterRequest
import model.auth.request.SocialMedia
import model.auth.response.LoginResponse
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

}