package db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Insert
    suspend fun insert(notificationEntity: NotificationEntity)

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM notifications")
    fun getAllNotificationEntity(): Flow<List<NotificationEntity>>

    @Query("DELETE FROM notifications")
    suspend fun deleteAll()

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markIsRead(id: Long)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun allMark()
}