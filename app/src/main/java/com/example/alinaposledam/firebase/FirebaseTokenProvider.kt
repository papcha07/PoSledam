package com.example.alinaposledam.firebase

import android.util.Log
import apiService.AuthService
import apiService.models.auth_models.DeviceTokenRequest
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import storage.TokenRepository

class FirebaseTokenProvider(
    private val authService: AuthService,
    private val tokenRepository: TokenRepository,
    private val deviceTokenStore: FirebaseDeviceTokenStore
) {
    private val syncMutex = Mutex()

    suspend fun saveTokenAndSendIfAuthorized(token: String) {
        deviceTokenStore.saveToken(token)
        sendStoredTokenIfAuthorized(fetchIfMissing = false)
    }

    suspend fun sendCurrentTokenToServer() {
        sendStoredTokenIfAuthorized(fetchIfMissing = true)
    }

    private suspend fun sendStoredTokenIfAuthorized(fetchIfMissing: Boolean) {
        syncMutex.withLock {
            val authorizationToken = tokenRepository.getToken()
            if (authorizationToken.isNullOrBlank()) {
                Log.d("FCM", "Device token sync skipped: user is not authorized")
                return
            }

            val token = getStoredOrFreshToken(fetchIfMissing) ?: run {
                Log.d("FCM", "Device token sync skipped: token is missing")
                return
            }

            if (deviceTokenStore.isTokenSent(token, authorizationToken)) {
                Log.d("FCM", "Device token sync skipped: token already sent")
                return
            }

            deviceTokenStore.markTokenSendAttempt()
            val isSent = authService.sendDeviceToken(
                DeviceTokenRequest(deviceToken = token)
            )

            if (isSent) {
                deviceTokenStore.markTokenSent(token, authorizationToken)
                Log.d("FCM", "Device token sent")
            } else {
                Log.w("FCM", "Device token was not accepted by server")
            }
        }
    }

    private suspend fun getStoredOrFreshToken(fetchIfMissing: Boolean): String? {
        val storedToken = deviceTokenStore.getToken()
        if (!storedToken.isNullOrBlank()) return storedToken
        if (!fetchIfMissing) return null

        try {
            val token = FirebaseMessaging.getInstance().token.await()
            deviceTokenStore.saveToken(token)
            return token
        } catch (e: Exception) {
            Log.e("FCM", "Failed to fetch device token", e)
            return null
        }
    }
}
