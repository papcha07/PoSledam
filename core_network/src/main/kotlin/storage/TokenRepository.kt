package storage

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface TokenRepository {

    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
    suspend fun deleteToken()

    class Base(private val sharedPreferences: SharedPreferences) : TokenRepository {

        override suspend fun saveToken(token: String) {
            sharedPreferences.edit { putString(TOKEN_KEY, token) }
        }

        override suspend fun getToken(): String? = withContext(Dispatchers.IO) {
            sharedPreferences.getString(TOKEN_KEY, null)
        }

        override suspend fun deleteToken() {
            withContext(Dispatchers.IO) {
                sharedPreferences
                    .edit()
                    .remove(TOKEN_KEY)
                    .apply()
            }
        }

        companion object {
            private val TOKEN_KEY = "token_key"
        }
    }

}