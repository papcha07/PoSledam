package repository

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import withIo

class NotificationSettingsRepositoryImpl(
    private val context: Context
) : NotificationSettingsRepository {

    private val prefs by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val _state = MutableStateFlow(readFromPrefs())
    private fun readFromPrefs(): Boolean =
        prefs.getBoolean(KEY_ENABLED, false)

    override fun observeNotificationsEnabled(): Flow<Boolean> = _state.asStateFlow()

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        withIo {
            prefs.edit().putBoolean(KEY_ENABLED, enabled).apply()
        }
        _state.value = enabled
    }

    override suspend fun isNotificationsEnabled(): Boolean {
        return withIo { readFromPrefs() }
    }

    companion object {
        private const val PREFS_NAME = "notification_settings"
        private const val KEY_ENABLED = "notifications_enabled"
    }
}

