package domain.repository

interface LocationRepository {
    suspend fun sendCurrentLocation(latitude: Double, longitude: Double)
}

