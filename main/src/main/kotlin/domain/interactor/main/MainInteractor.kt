package domain.interactor.main

import ApiResponse
import ui.model.UserDataUiInfo

interface MainInteractor {
    suspend fun getUserFromCache(): UserDataUiInfo?
    suspend fun syncUserFromServer(): ApiResponse<UserDataUiInfo>
    suspend fun deleteUser()
    suspend fun updateUserInfo(userDataInfo: UserDataUiInfo)
    suspend fun updateUserImage(uri: String, id: String)
}

