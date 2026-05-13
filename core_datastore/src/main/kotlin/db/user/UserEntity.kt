package db.user

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val firstName: String,
    val description: String,
    val avatarPath: String?,
    val tg: String?,
    val wh: String?,
    val vk: String?,
)
