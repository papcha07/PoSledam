package model.announcement

data class MissAllRequest(
    val lastDateTime: String? = null,
    val district: String? = null,
    val type: Int? = null,
    val gender: Int? = null,
    val searchRadius: Int? = null,
    val searchCenterLatitude: Double? = null,
    val searchCenterLongitude: Double? = null
)
