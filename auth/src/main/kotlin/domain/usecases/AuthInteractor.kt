package usecases

import domain.model.LoginInfo
import ui.model.data.UserDataInfo

interface AuthInteractor {
    suspend fun register(registerInfo: UserDataInfo): Pair<Boolean, Int?>
    suspend fun login(loginInfo: LoginInfo): Pair<Boolean, Int?>
}