sealed interface ApiResponse<out T> {
    data class Success<out T>(val data: T) : ApiResponse<T>
    data class Error(val errorCode: Int) : ApiResponse<Nothing>
}