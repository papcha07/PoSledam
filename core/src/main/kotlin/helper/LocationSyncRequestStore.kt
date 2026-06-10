package helper

import android.content.Context
import androidx.core.content.edit

interface LocationSyncRequestStore {
    fun markSendAfterLoginPending()
    fun isSendAfterLoginPending(): Boolean
    fun clearSendAfterLoginPending()
}

class SharedPreferencesLocationSyncRequestStore(
    context: Context
) : LocationSyncRequestStore {
    private val preferences = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    override fun markSendAfterLoginPending() {
        preferences.edit { putBoolean(KEY_SEND_AFTER_LOGIN_PENDING, true) }
    }

    override fun isSendAfterLoginPending(): Boolean {
        return preferences.getBoolean(KEY_SEND_AFTER_LOGIN_PENDING, false)
    }

    override fun clearSendAfterLoginPending() {
        preferences.edit { remove(KEY_SEND_AFTER_LOGIN_PENDING) }
    }

    private companion object {
        const val PREFS_NAME = "location_sync_requests"
        const val KEY_SEND_AFTER_LOGIN_PENDING = "send_after_login_pending"
    }
}
