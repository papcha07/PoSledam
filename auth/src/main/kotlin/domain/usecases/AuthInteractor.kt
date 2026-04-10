package usecases

import domain.model.AuthResult
import domain.model.LoginInfo
import domain.model.LoginResult
import domain.model.RegisterInfo
import kotlinx.coroutines.flow.Flow
import ui.model.UserDataInfo

interface AuthInteractor {
    suspend fun register(registerInfo: UserDataInfo) : Flow<Pair<Boolean, Int?>>
    suspend fun login(loginInfo: LoginInfo) : Flow<Pair<Boolean, Int?>>
}