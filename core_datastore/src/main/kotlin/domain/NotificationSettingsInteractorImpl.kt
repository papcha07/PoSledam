package domain

import kotlinx.coroutines.flow.Flow
import repository.NotificationSettingsRepository

class NotificationSettingsInteractorImpl(
    private val repository: NotificationSettingsRepository
) : NotificationSettingsInteractor {

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        repository.setNotificationsEnabled(enabled)
    }

    override suspend fun isNotificationsEnabled(): Boolean {
        return repository.isNotificationsEnabled()
    }

    override fun observeNotificationsEnabled(): Flow<Boolean> {
        return repository.observeNotificationsEnabled()
    }
}

