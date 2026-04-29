package domain.repository

import domain.model.LoginInfo
import ui.model.data.UserDataInfo

interface AuthRepository {
    suspend fun register(registerInfo: UserDataInfo): Pair<Boolean, Int?>
    suspend fun login(loginInfo: LoginInfo): Pair<Boolean, Int?>
}