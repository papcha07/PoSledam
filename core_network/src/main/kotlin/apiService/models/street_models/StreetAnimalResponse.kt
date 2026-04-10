package apiService.models.street_models

import kotlinx.serialization.Serializable
import model.announcement.Location


@Serializable
data class StreetAnimalResponse(
    val id: String,
    val street: String?,
    val house: String?,
    val district: String?,
    val mainImagePath: String,
    val petType: Int,
    val location: Location,
    val eventDate: String,
    val createdAt: String,
    val placeDescription: String?
)
