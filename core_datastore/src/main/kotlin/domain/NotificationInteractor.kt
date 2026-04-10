package domain

import domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationInteractor {
    suspend fun insert(notification: Notification)
    suspend fun deleteById(id: Long)
    fun getAllNotificationEntity(): Flow<List<Notification>>
    suspend fun deleteAll()
    suspend fun markIsRead(id: Long)

    suspend fun allMark()
}