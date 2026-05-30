package model.announcement

data class MissAllRequest(
    val lastDateTime: String? = null,
    val district: String? = null,
    val from: String? = null,
    val type: Int? = null,
    val gender: Int? = null,
    val searchRadius: Int? = null,
    val searchCenterLatitude: Int? = null,
    val searchCenterLongitude: Int? = null
)
