package model.announcement

import kotlinx.serialization.Serializable

@Serializable
data class FoundPetResponse(
    val id: String,
    val street: String?,
    val house: String?,
    val district: String? = null,
    val imagesPaths: List<String>?,
    val creator: CreatorDto,
    val location: Location,
    val eventDate: String,
    val petType: Int,
    val gender: Int,
    val color: String? = null,
    val breed: String? = null,
    val type: Int,
    val description: String? = null,
) {
    @Serializable
    data class CreatorDto(
        val id: String,
        val firstName: String? = null,
        val avatarPath: String? = null,
        val description: String? = null,
        val contacts: List<Contacts>? = null
    )

    @Serializable
    data class Location(
        val longitude: Double,
        val latitude: Double
    )

    @Serializable
    data class Contacts(
        val contactType: Int? = null,
        val url: String? = null
    )
}
