package model.geo

import kotlinx.serialization.Serializable

@Serializable
data class AddressSuggestion(
    val city: String?,
    val district: String?,
    val house: String?,
    val address: String,
    val lon: Double?,
    val lat: Double?
)