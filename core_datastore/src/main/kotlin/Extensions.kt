import androidx.core.net.toUri
import apiService.models.auth_models.UserInfoResponse
import db.notification.NotificationEntity
import db.user.UserEntity
import domain.notification.Notification
import domain.user.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

suspend fun <T> withIo(block: suspend () -> T): T {
    return withContext(Dispatchers.IO) {
        block()
    }
}

fun Long.toFormattedDate(
    pattern: String = "dd.MM.yyyy"
): String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(Date(this))
}

fun Notification.toEntity(): NotificationEntity {
    return NotificationEntity(
        title = title,
        body = body,
        timestamp = time,
        isRead = isRead,
        type = type
    )
}

fun NotificationEntity.toDomain(): Notification {
    return Notification(
        id = id,
        title = title,
        body = body,
        time = timestamp,
        isRead = isRead,
        type = type,
    )
}

fun UserInfoResponse.toEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        firstName = this.firstName,
        description = this.description,
        avatarPath = this.avatarPath,
        tg = this.contacts?.getOrNull(0)?.url,
        wh = this.contacts?.getOrNull(2)?.url,
        vk = this.contacts?.getOrNull(1)?.url
    )
}


fun UserEntity.toDomain(): User {
    return User(
        name = this.firstName,
        description = this.description,
        avatarPath = this.avatarPath?.toUri(),
        tg = this.tg,
        wh = this.wh,
        vk = this.vk
    )
}


