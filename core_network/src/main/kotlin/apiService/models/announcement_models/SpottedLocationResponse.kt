package apiService.models.announcement_models

import kotlinx.serialization.Serializable

@Serializable
data class SpottedLocationResponse(
    val id: String,
    val spottedUser: SpottedUserResponse,
    val createdAt: String,
    val location: SpottedLocationDto,
    val imagesPath: List<String>? = null
)

@Serializable
data class SpottedUserResponse(
    val id: String,
    val firstName: String? = null,
    val secondName: String? = null
)

@Serializable
data class SpottedLocationDto(
    val latitude: Double,
    val longitude: Double
)
