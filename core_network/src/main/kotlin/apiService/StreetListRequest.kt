package apiService

data class StreetListRequest(
    val lastDateTime: String?,
    val from: String? = null,
    val type: Int? = null
)