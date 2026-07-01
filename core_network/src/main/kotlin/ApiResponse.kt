sealed interface ApiResponse<out T> {
    data class Success<out T>(val data: T) : ApiResponse<T>
    data class Error(
        val errorCode: Int,
        val errorDetails: model.errorResponse.ErrorDetails? = null
    ) : ApiResponse<Nothing>
}
