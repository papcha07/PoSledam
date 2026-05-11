package apiService.models.street_models

import kotlinx.serialization.Serializable

@Serializable
data class StreetAnimalDetailsResponse(
    val street: String,
    val house: String,
    val imagesPaths: List<String>,
    val creator: CreatorDto,
    val location: Location,
    val eventDate: String,
    val placeDescription: String,
) {
    @Serializable
    data class CreatorDto(
        val id: String,
        val firstName: String,
        val avatarPath: String? = null
    )

    @Serializable
    data class Location(
        val longitude: Double,
        val latitude: Double
    )
}

