package domain.interactor

import ApiResponse
import domain.models.AdvertInfo
import ui.model.UserDataInfo
import ui.model.UserDataUiInfo

interface MainInteractor {
    suspend fun getUserFromCache(): UserDataUiInfo?
    suspend fun syncUserFromServer(): ApiResponse<UserDataUiInfo>
    suspend fun deleteUser()
    suspend fun updateUserInfo(userDataInfo: UserDataUiInfo)
    suspend fun updateUserImage(uri: String, id: String)
}

