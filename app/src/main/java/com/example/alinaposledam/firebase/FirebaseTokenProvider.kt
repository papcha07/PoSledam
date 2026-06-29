package com.example.alinaposledam.firebase

import android.util.Log
import apiService.AuthService
import apiService.models.auth_models.DeviceTokenRequest
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

class FirebaseTokenProvider(
    private val authService: AuthService
) {
    suspend fun sendCurrentTokenToServer() {
        try {
            val token = FirebaseMessaging.getInstance().token.await()
            authService.sendDeviceToken(
                DeviceTokenRequest(deviceToken = token)
            )
            Log.d("FCM", "Device token sent")
        } catch (e: Exception) {
            Log.e("FCM", "Failed to send device token", e)
        }
    }
}
