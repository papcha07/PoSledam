package repository

import db.NotificationDao
import db.NotificationEntity
import kotlinx.coroutines.flow.Flow
import withIo

class NotificationRepositoryImpl(
    private val notificationDao: NotificationDao
) : NotificationRepository {

    override fun getAllNotificationEntity(): Flow<List<NotificationEntity>> =
        notificationDao.getAllNotificationEntity()

    override suspend fun insert(notificationEntity: NotificationEntity) =
        withIo { notificationDao.insert(notificationEntity) }

    override suspend fun deleteById(id: Long) =
        withIo { notificationDao.deleteById(id) }

    override suspend fun deleteAll() =
        withIo { notificationDao.deleteAll() }

    override suspend fun markIsRead(id: Long) =
        withIo { notificationDao.markIsRead(id) }

    override suspend fun allMark() =
        withIo { notificationDao.allMark() }
}


