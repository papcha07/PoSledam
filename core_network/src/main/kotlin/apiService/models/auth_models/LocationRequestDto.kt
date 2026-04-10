package apiService.models.auth_models

import kotlinx.serialization.Serializable

@Serializable
data class LocationRequestDto(
    val latitude: Double,
    val longitude: Double
)
