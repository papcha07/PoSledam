package com.example.alinaposledam.firebase

import android.content.Context
import androidx.core.content.edit
import java.security.MessageDigest

interface FirebaseDeviceTokenStore {
    fun saveToken(token: String)
    fun getToken(): String?
    fun isTokenSent(token: String, authorizationToken: String): Boolean
    fun markTokenSendAttempt()
    fun markTokenSent(token: String, authorizationToken: String)
}

class SharedPreferencesFirebaseDeviceTokenStore(
    context: Context
) : FirebaseDeviceTokenStore {
    private val preferences = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    override fun saveToken(token: String) {
        preferences.edit {
            putString(KEY_LAST_TOKEN, token)
        }
    }

    override fun getToken(): String? {
        return preferences.getString(KEY_LAST_TOKEN, null)
    }

    override fun isTokenSent(token: String, authorizationToken: String): Boolean {
        return preferences.getString(KEY_SENT_TOKEN, null) == token &&
                preferences.getString(KEY_SENT_AUTH_MARKER, null) == authorizationToken.sha256()
    }

    override fun markTokenSendAttempt() {
        preferences.edit {
            putLong(KEY_LAST_ATTEMPT_AT, System.currentTimeMillis())
        }
    }

    override fun markTokenSent(token: String, authorizationToken: String) {
        preferences.edit {
            putString(KEY_SENT_TOKEN, token)
            putString(KEY_SENT_AUTH_MARKER, authorizationToken.sha256())
        }
    }

    private fun String.sha256(): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(toByteArray(Charsets.UTF_8))
        return bytes.joinToString(separator = "") { byte ->
            "%02x".format(byte.toInt() and 0xff)
        }
    }

    private companion object {
        const val PREFS_NAME = "firebase_device_token"
        const val KEY_LAST_TOKEN = "last_token"
        const val KEY_SENT_TOKEN = "sent_token"
        const val KEY_SENT_AUTH_MARKER = "sent_auth_marker"
        const val KEY_LAST_ATTEMPT_AT = "last_attempt_at"
    }
}
