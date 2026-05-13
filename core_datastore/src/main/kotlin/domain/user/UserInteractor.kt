package domain.user

import domain.user.model.User
import kotlinx.coroutines.flow.Flow

interface UserInteractor {
    suspend fun observeUser(): Flow<User?>
    suspend fun refreshUser()
}