package domain.interactor.location

interface LocationInteractor {
    suspend fun sendCurrentLocation(): LocationSendResult
}

sealed interface LocationSendResult {
    data object Success : LocationSendResult
    data object PermissionDenied : LocationSendResult
    data object LocationUnavailable : LocationSendResult
    data class NetworkError(val retryable: Boolean) : LocationSendResult
}
