package usecases

import domain.model.LoginInfo
import domain.model.AuthOperationResult
import domain.model.LoginResult
import ui.model.data.UserDataInfo

interface AuthInteractor {
    suspend fun register(registerInfo: UserDataInfo): Pair<Boolean, Int?>
    suspend fun login(loginInfo: LoginInfo): LoginResult
    suspend fun resendEmailConfirmation(email: String): AuthOperationResult
}
