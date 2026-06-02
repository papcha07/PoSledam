package domain.user

import domain.user.model.LocationDto
import domain.user.model.User
import kotlinx.coroutines.flow.Flow
import model.announcement.Location

interface UserRepository {
    suspend fun updateUser(user: User)
    fun observeUser(): Flow<User?>
    fun observeLocation() : Flow<LocationDto?>
    suspend fun refreshUser()
    suspend fun clearUser()
    suspend fun updateUserLocation(latitude: Double, longitude: Double)

}