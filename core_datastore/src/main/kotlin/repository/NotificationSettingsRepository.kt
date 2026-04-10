package repository

import kotlinx.coroutines.flow.Flow

interface NotificationSettingsRepository {
    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun isNotificationsEnabled(): Boolean
    fun observeNotificationsEnabled(): Flow<Boolean>
}

