package domain.interactor

interface LocationInteractor {
    suspend fun sendCurrentLocation()
}

