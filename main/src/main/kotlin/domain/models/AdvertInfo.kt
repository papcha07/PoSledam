package domain.models

import android.net.Uri

data class AdvertInfo(
    val images: List<Uri> = listOf(),
    val petType: Int = 2,
    val lat: Double = 32.0,
    val lon: Double = 32.0,
    val eventDate: String = "",
    val eventDateUtc: String = "",
    val address: String = "",
    val placeDescription: String = "",
    val isPlaced: Boolean? = false
)