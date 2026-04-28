package domain.repository

import domain.model.LoginInfo
import kotlinx.coroutines.flow.Flow
import ui.model.UserDataInfo

interface AuthRepository {
    suspend fun register(registerInfo: UserDataInfo): Pair<Boolean, Int?>
    suspend fun login(loginInfo: LoginInfo): Flow<Pair<Boolean, Int?>>
}