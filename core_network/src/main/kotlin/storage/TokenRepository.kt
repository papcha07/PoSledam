package storage

import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.content.edit

interface TokenRepository {

    suspend fun saveToken(token: String)
    suspend fun getToken(): String?

    class Base(private val sharedPreferences: SharedPreferences) : TokenRepository {

        override suspend fun saveToken(token: String) {
            sharedPreferences.edit { putString(TOKEN_KEY, token) }
        }

        override suspend fun getToken(): String? = withContext(Dispatchers.IO) {
            sharedPreferences.getString(TOKEN_KEY, null)
        }

        companion object {
            private val TOKEN_KEY = "token_key"
        }
    }

}