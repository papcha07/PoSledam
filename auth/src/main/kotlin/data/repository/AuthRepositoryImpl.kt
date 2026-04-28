package data.repository

import ApiResponse
import apiService.AuthService
import domain.model.LoginInfo
import domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import model.auth.request.LoginRequest
import model.auth.request.RegisterRequest
import model.auth.request.SocialMedia
import model.auth.response.LoginResponse
import model.auth.response.RegisterResponse
import ui.model.UserDataInfo

class AuthRepositoryImpl(
    private val apiService: AuthService,
) : AuthRepository {

    override suspend fun register(registerInfo: UserDataInfo): Pair<Boolean, Int?> {
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

        return when (resultOfResponse) {
            is ApiResponse.Error -> Pair(false, resultOfResponse.errorCode)
            is ApiResponse.Success<RegisterResponse> -> Pair(true, null)
        }
    }


    override suspend fun login(loginInfo: LoginInfo): Flow<Pair<Boolean, Int?>> = flow {
        val result = apiService.login(
            loginRequest = LoginRequest(
                email = loginInfo.email,
                password = loginInfo.password
            )
        )
        when (result) {
            is ApiResponse.Error -> {
                emit(Pair(false, result.errorCode))
            }

            is ApiResponse.Success<LoginResponse> -> {
                emit(Pair(true, null))
            }
        }
    }

}