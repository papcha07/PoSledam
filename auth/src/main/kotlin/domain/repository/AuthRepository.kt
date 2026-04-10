package domain.repository

import domain.model.LoginInfo
import domain.model.RegisterInfo
import kotlinx.coroutines.flow.Flow
import model.InternetStatus
import ui.model.UserDataInfo

interface AuthRepository {
    suspend fun register(registerInfo: UserDataInfo): Flow<Pair<Boolean, Int?>>
    suspend fun login(loginInfo: LoginInfo): Flow<Pair<Boolean, Int?>>
}