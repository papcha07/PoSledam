package model.announcement

import kotlinx.serialization.Serializable

@Serializable
data class MissAllDto(
    val id: String,
    val createdAt: String? = null,
    val eventDate: String? = null,
    val petName: String? = null,
    val description: String? = null,
    val mainImagePath: String? = null,
    val district: String? = null,
    val breed: String? = null,
)
