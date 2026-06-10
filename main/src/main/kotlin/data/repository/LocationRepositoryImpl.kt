package data.repository

import SendResult
import apiService.AuthService
import apiService.models.auth_models.LocationRequestDto
import domain.repository.LocationRepository

class LocationRepositoryImpl(
    private val authService: AuthService
) : LocationRepository {

    override suspend fun sendCurrentLocation(latitude: Double, longitude: Double): SendResult {
        return authService.sendCurrentLocation(
            LocationRequestDto(
                latitude = latitude,
                longitude = longitude
            )
        )
    }
}
