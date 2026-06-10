package domain.repository

import SendResult

interface LocationRepository {
    suspend fun sendCurrentLocation(latitude: Double, longitude: Double): SendResult
}
