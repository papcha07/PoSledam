package model.announcement

import kotlinx.serialization.Serializable

@Serializable
data class MissAllDto(
    val id: String,
    val createdAt: String? = null,
    val eventDate: String? = null,
    val petName: String?,
    val description: String?,
    val mainImagePath: String?,
    val district: String?,
    val breed: String?,
)

