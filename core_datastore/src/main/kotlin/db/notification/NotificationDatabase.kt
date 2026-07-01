package db.notification

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [NotificationEntity::class], version = 2, exportSchema = false)
abstract class NotificationDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao

    companion object {
        val MIGRATION_1_2_L = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
            ALTER TABLE `notifications` 
            ADD COLUMN `announcementId` TEXT NOT NULL DEFAULT ''
            """.trimIndent()
                )
            }
        }
    }

}
