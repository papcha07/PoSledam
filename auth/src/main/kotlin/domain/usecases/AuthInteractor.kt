package usecases

import domain.model.LoginInfo
import domain.model.AuthOperationResult
import ui.model.data.UserDataInfo

interface AuthInteractor {
    suspend fun register(registerInfo: UserDataInfo): Pair<Boolean, Int?>
    suspend fun login(loginInfo: LoginInfo): Pair<Boolean, Int?>
    suspend fun resendEmailConfirmation(email: String): AuthOperationResult
}
