package data.repository

import ApiResponse
import android.util.Log
import apiService.AuthService
import apiService.models.auth_models.UserInfoResponse
import apiService.models.auth_models.UpdateUserInfoRequest
import domain.UserInfo
import domain.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import storage.UserInfoRepository
import java.io.File

class MainRepositoryImpl(
    private val authService: AuthService,
    private val userInfoRepository: UserInfoRepository
) : MainRepository {

    override suspend fun saveUserToCached(userInfo: UserInfo) {
        withContext(Dispatchers.IO) {
            userInfoRepository.saveUserInfo(userInfo)
        }
    }

    override suspend fun updateUserInfo(
        updateUserInfoRequest: UpdateUserInfoRequest,
        userInfo: UserInfo
    ): Boolean {
        userInfoRepository.saveUserInfo(userInfo)
        return authService.updateUserInfo(updateUserInfoRequest)
    }

    override suspend fun getUserFromCache(): UserInfo? = withContext(Dispatchers.IO) {
        userInfoRepository.getUserInfo()
    }

    override suspend fun syncUserFromServer(): ApiResponse<UserInfo> {
        return when (val response = authService.getInfo()) {

            is ApiResponse.Error -> {
                ApiResponse.Error(response.errorCode)
            }

            is ApiResponse.Success<UserInfoResponse> -> {
                val mapped = mapToUserInfo(response.data)
                Log.d("savedUser", mapped.avatarPath.toString())
                withContext(Dispatchers.IO) {
                    userInfoRepository.saveUserInfo(mapped)
                }
                ApiResponse.Success(mapped)
            }
        }
    }

    override suspend fun deleteUser() {
        withContext(Dispatchers.IO) {
            userInfoRepository.deleteUserInfo()
        }
    }

    override suspend fun updateUserImage(file: File, id: String) {
        authService.updateUserImage(file, id)
        val response = authService.getInfo()
        when (response) {
            is ApiResponse.Error -> {
                Log.d("updateImageError", "упало при попытке получить пользователя")
            }

            is ApiResponse.Success<UserInfoResponse> -> {
                val mapped = mapToUserInfo(response.data)
                withContext(Dispatchers.IO) {
                    userInfoRepository.saveUserInfo(mapped)
                }
            }
        }
    }
}


private fun mapToUserInfo(userInfoResponse: UserInfoResponse): UserInfo {
    Log.d("userInfoResponse", userInfoResponse.id)
    return UserInfo(
        id = userInfoResponse.id,
        contacts = userInfoResponse.contacts,
        firstName = userInfoResponse.firstName,
        avatarPath = userInfoResponse.avatarPath,
        description = userInfoResponse.description
    )
}
