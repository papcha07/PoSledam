sealed class SendResult {
    data object Success : SendResult()
    data class BadRequest(val message: String = "Bad request") : SendResult()
    data class Error(val message: String) : SendResult()
}

enum class AnnouncementType {
    Miss,
    Found
}