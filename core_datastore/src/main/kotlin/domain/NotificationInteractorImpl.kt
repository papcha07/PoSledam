package domain

import domain.model.Notification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import repository.NotificationRepository
import toDomain
import toEntity

class NotificationInteractorImpl(
    private val notificationRepository: NotificationRepository
) : NotificationInteractor {

    override suspend fun insert(notification: Notification) {
        val convertedNotification = notification.toEntity()
        notificationRepository.insert(convertedNotification)
    }

    override suspend fun deleteById(id: Long) {
        notificationRepository.deleteById(id)
    }

    override fun getAllNotificationEntity(): Flow<List<Notification>> =
        notificationRepository.getAllNotificationEntity().map { list ->
            list.map {
                it.toDomain()
            }
        }

    override suspend fun deleteAll() {
        notificationRepository.deleteAll()
    }

    override suspend fun markIsRead(id: Long) {
        notificationRepository.markIsRead(id)
    }

    override suspend fun allMark() {
        notificationRepository.allMark()
    }
}
