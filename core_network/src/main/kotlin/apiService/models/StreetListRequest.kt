package apiService.models

data class StreetListRequest(
    val lastDateTime: String?,
    val from: String? = null,
    val type: Int? = null,
    val centerRadius: Int? = null,
    val searchCenterLatitude: Double? = null,
    val searchCenterLongitude: Double? = null
)
