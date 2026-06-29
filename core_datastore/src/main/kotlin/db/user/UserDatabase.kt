package db.user

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities = [UserEntity::class, LocationEntity::class], version = 3, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun locationDao() : LocationDao
    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE user ADD COLUMN latitude REAL")
                db.execSQL("ALTER TABLE user ADD COLUMN longitude REAL")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS `location` (
                `id` INTEGER NOT NULL,
                `latitude` REAL NOT NULL,
                `longitude` REAL NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
                )

                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS `user_new` (
                `id` TEXT NOT NULL,
                `firstName` TEXT NOT NULL,
                `description` TEXT,
                `avatarPath` TEXT,
                `tg` TEXT,
                `wh` TEXT,
                `vk` TEXT,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
                )

                db.execSQL(
                    """
            INSERT INTO `user_new` (
                id, firstName, description, avatarPath, tg, wh, vk
            )
            SELECT 
                id, firstName, description, avatarPath, tg, wh, vk
            FROM `user`
            """.trimIndent()
                )

                db.execSQL("DROP TABLE `user`")
                db.execSQL("ALTER TABLE `user_new` RENAME TO `user`")
            }
        }
    }
}
