package domain.interactor.location

interface LocationInteractor {
    suspend fun sendCurrentLocation()
}