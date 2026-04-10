import db.NotificationEntity
import domain.model.Notification
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

