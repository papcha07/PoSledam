package storage

import android.content.SharedPreferences
import androidx.core.content.edit
import domain.UserInfo
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface UserInfoRepository {
    suspend fun saveUserInfo(userInfo: UserInfo)
    suspend fun getUserInfo(): UserInfo?
    suspend fun deleteUserInfo()

    class Base(
        private val sharedPreferences: SharedPreferences,
        private val json: Json
    ) : UserInfoRepository {

        override suspend fun saveUserInfo(userInfo: UserInfo) {
            val userJson = json.encodeToString(userInfo)
            sharedPreferences.edit { putString(USER_KEY, userJson) }
        }

        override suspend fun getUserInfo(): UserInfo? {
            val userJson = sharedPreferences.getString(USER_KEY, null) ?: return null
            return json.decodeFromString(userJson)
        }

        override suspend fun deleteUserInfo() {
            sharedPreferences.edit {
                remove(USER_KEY)
            }
        }

        companion object {
            private const val USER_KEY = "user_key"
        }

    }
}