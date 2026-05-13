package domain.user

import domain.user.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeUser(): Flow<User?>
    suspend fun refreshUser()
    suspend fun clearUser()
}