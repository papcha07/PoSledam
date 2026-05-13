package domain.notification

import kotlinx.coroutines.flow.Flow

interface NotificationSettingsInteractor {
    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun isNotificationsEnabled(): Boolean
    fun observeNotificationsEnabled(): Flow<Boolean>
}

