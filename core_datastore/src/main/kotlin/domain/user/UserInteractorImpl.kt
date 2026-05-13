package domain.user

import domain.user.model.User
import kotlinx.coroutines.flow.Flow

class UserInteractorImpl(
    private val userRepository: UserRepository
) : UserInteractor {

    override suspend fun observeUser(): Flow<User?> {
        return userRepository.observeUser()
    }

    override suspend fun refreshUser() {
        userRepository.refreshUser()
    }
}