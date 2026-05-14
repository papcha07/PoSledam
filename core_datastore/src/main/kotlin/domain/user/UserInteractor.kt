package domain.user

import domain.user.model.User
import kotlinx.coroutines.flow.Flow

interface UserInteractor {
    suspend fun updateUserInfo(user: User)
    fun observeUser(): Flow<User?>
    suspend fun refreshUser()
    suspend fun clearUser()
}