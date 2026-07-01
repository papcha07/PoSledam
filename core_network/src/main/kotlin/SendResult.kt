sealed class SendResult {
    data object Success : SendResult()
    data class BadRequest(
        val message: String = "Bad request",
        val statusCode: Int? = null,
        val errorDetails: model.errorResponse.ErrorDetails? = null
    ) : SendResult()
    data class Error(val message: String) : SendResult()
}

enum class AnnouncementType {
    Miss,
    Found
}
