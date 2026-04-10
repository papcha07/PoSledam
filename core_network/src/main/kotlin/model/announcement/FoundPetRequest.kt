package model.announcement

import kotlinx.serialization.Serializable

@Serializable
data class FoundPetRequest(
    val id : String
)
