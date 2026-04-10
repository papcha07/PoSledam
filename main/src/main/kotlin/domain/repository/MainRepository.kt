package domain.repository

import ApiResponse
import apiService.models.auth_models.UpdateUserInfoRequest
import domain.UserInfo
import domain.models.AdvertInfo
import java.io.File

interface MainRepository {
    suspend fun getUserFromCache(): UserInfo?
    suspend fun syncUserFromServer(): ApiResponse<UserInfo>
    suspend fun deleteUser()
    suspend fun updateUserImage(file: File, id: String)
    suspend fun saveUserToCached(userInfo: UserInfo)
    suspend fun updateUserInfo(updateUserInfoRequest: UpdateUserInfoRequest, userInfo: UserInfo) : Boolean
}
