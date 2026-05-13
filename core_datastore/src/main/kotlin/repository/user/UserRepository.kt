package repository.user

import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun observeUser(): Flow<User>
}