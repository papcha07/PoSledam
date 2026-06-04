package com.example.alinaposledam.firebase

import android.util.Log
import apiService.AuthService
import apiService.models.auth_models.DeviceTokenRequest
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MyFirebaseMessagingService : FirebaseMessagingService(), KoinComponent {

    private val authService: AuthService by inject()

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        serviceScope.launch {
            sendTokenToServer(token)
        }
    }

    private suspend fun sendTokenToServer(token: String) {
        try {
            authService.sendDeviceToken(
                DeviceTokenRequest(deviceToken = token)
            )
        } catch (e: Exception) {
            Log.e("FCM", "Failed to send device token", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
}