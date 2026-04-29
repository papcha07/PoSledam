package usecases

import domain.model.LoginInfo
import domain.repository.AuthRepository
import ui.model.data.UserDataInfo

class AuthInteractorImpl(
    private val authRepository: AuthRepository,
) : AuthInteractor {

    override suspend fun register(registerInfo: UserDataInfo) =
        authRepository.register(registerInfo)

    override suspend fun login(loginInfo: LoginInfo): Pair<Boolean, Int?> = authRepository.login(loginInfo)
}