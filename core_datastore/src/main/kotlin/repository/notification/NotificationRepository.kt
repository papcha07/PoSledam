package repository.notification

import db.notification.NotificationEntity
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun insert(notificationEntity: NotificationEntity)
    suspend fun deleteById(id: Long)
    fun getAllNotificationEntity(): Flow<List<NotificationEntity>>
    suspend fun deleteAll()
    suspend fun markIsRead(id: Long)
    suspend fun allMark()
}