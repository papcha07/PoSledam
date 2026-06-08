package com.example.alinaposledam.firebase

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import apiService.AuthService
import apiService.models.auth_models.DeviceTokenRequest
import com.example.alinaposledam.MainActivity
import com.example.core.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import domain.notification.Notification
import domain.notification.NotificationInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MyFirebaseMessagingService : FirebaseMessagingService(), KoinComponent {

    private val authService: AuthService by inject()
    private val notificationInteractor: NotificationInteractor by inject()
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        serviceScope.launch {
            sendTokenToServer(token)
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.notification?.title
            ?: message.data["title"]
            ?: "Новое уведомление"

        val body = message.notification?.body
            ?: message.data["body"]
            ?: "У вас новое сообщение"

        serviceScope.launch {
            notificationInteractor.insert(
                Notification(
                    id = 0,
                    title = title,
                    body = body,
                    isRead = false,
                    type = 1,
                    time = System.currentTimeMillis()
                )
            )
        }

        val notificationType = message.data["notification_type"]!!
        val entityId = message.data["entity_id"]!!
        when (notificationType) {
            REPORT_SPOTTED -> showNotificationSpotted(
                title = title,
                body = body,
                entityId = entityId,
                notificationType = notificationType
            )

            MISS_CREATED -> {
                showNotificationSpotted(
                    title = title,
                    body = body,
                    entityId = entityId,
                    notificationType = notificationType
                )
            }

            REPORT_FOUND -> {}
            else -> showNotification(title, body)

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

    private fun showNotification(
        title: String,
        body: String,
    ) {
        val channelId = "default_channel"

        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Основные уведомления",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_lapa)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showNotificationSpotted(
        title: String,
        body: String,
        entityId: String,
        notificationType: String
    ) {
        val channelId = "spotted_channel"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Уведомления о заметках пользователей",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            action = "OPEN_FROM_NOTIFICATION"

            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP

            putExtra("notification_type", notificationType)
            putExtra("entity_id", entityId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            entityId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_lapa)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(this).notify(
            entityId.hashCode(),
            notification
        )
    }


    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }


    companion object {
        private const val REPORT_SPOTTED = "ReportSpotted"
        private const val REPORT_FOUND = "ReportFound"

        private const val MISS_CREATED = "MissingAnnouncementCreated"
    }
}