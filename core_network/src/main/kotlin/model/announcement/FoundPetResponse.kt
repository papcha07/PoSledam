package model.announcement

import kotlinx.serialization.Serializable

@Serializable
data class FoundPetResponse(
    val id: String,
    val street: String,
    val house: String,
    val district: String? = null,
    val imagesPaths: List<String>?,
    val creator: CreatorDto,
    val location: Location,
    val eventDate: String,
    val petType: Int,
    val gender: Int,
    val color: String,
    val breed: String,
    val type: Int,
    val description: String,
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

