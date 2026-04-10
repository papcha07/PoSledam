package data.repository

import apiService.AuthService
import apiService.models.auth_models.LocationRequestDto
import domain.repository.LocationRepository

class LocationRepositoryImpl(
    private val authService: AuthService
) : LocationRepository {

    override suspend fun sendCurrentLocation(latitude: Double, longitude: Double) {
        authService.sendCurrentLocation(
            LocationRequestDto(
                latitude = latitude,
                longitude = longitude
            )
        )
    }
}

