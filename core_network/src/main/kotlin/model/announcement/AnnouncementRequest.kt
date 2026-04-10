package model.announcement

import kotlinx.serialization.Serializable

@Serializable
data class AnnouncementRequest(
    val location: Location,
    val petType: Int,
    val gender: Int,
    val color: String? = null,
    val breed: String? = null,
    val petName: String,
    val eventDate: String,
    val description: String? = null
)
