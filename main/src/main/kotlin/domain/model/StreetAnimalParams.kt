package domain.model

data class StreetAnimalParams(
    val from: String? = null,
    val type: Int? = null,
    val centerRadius: Int? = null,
    val searchCenterLatitude: Double? = null,
    val searchCenterLongitude: Double? = null
)
