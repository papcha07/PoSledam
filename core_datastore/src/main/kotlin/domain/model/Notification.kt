package domain.model

data class Notification(
    val id: Long,
    val title: String,
    val body: String,
    val isRead: Boolean,
    val type: Int,
    val time: Long
)
