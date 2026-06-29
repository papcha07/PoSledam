package usecases

import domain.model.LoginInfo
import domain.model.LoginResult
import domain.repository.AuthRepository
import ui.model.data.UserDataInfo

class AuthInteractorImpl(
    private val authRepository: AuthRepository,
) : AuthInteractor {

    override suspend fun register(registerInfo: UserDataInfo) =
        authRepository.register(registerInfo)

    override suspend fun login(loginInfo: LoginInfo): LoginResult =
        authRepository.login(loginInfo)

    override suspend fun resendEmailConfirmation(email: String) =
        authRepository.resendEmailConfirmation(email)
}
