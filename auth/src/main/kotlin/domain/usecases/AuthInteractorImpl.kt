package usecases

import domain.model.LoginInfo
import domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import storage.TokenRepository
import ui.model.UserDataInfo

class AuthInteractorImpl(
    private val authRepository: AuthRepository,
    private val tokenRepository: TokenRepository
) : AuthInteractor {

    override suspend fun register(registerInfo: UserDataInfo) =
        authRepository.register(registerInfo)

    override suspend fun login(loginInfo: LoginInfo): Flow<Pair<Boolean, Int?>> =
        authRepository.login(loginInfo)
}