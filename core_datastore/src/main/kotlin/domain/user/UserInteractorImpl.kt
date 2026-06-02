package domain.user

import domain.user.model.User
import kotlinx.coroutines.flow.Flow
import storage.TokenRepository

class UserInteractorImpl(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository
) : UserInteractor {
    override suspend fun updateUserInfo(user: User) {
        userRepository.updateUser(user)
    }

    override fun observeUser(): Flow<User?> {
        return userRepository.observeUser()
    }

    override suspend fun refreshUser() {
        userRepository.refreshUser()
    }

    override suspend fun clearUser() {
        userRepository.clearUser()
        tokenRepository.deleteToken()
    }

    override suspend fun updateUserLocation(latitude: Double, longitude: Double) {
        userRepository.updateUserLocation(latitude, longitude)
    }
}