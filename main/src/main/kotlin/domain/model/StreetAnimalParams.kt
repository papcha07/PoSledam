package domain.model

data class StreetAnimalParams(
    val from: String? = null,
    val type: Int? = null,
    val centerRadius: Int,
    val searchCenterLatitude: Double,
    val searchCenterLongitude: Double
)
