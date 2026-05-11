package domain.models

data class StreetDetails(
    val imagePath: List<String>,
    val creator: CreatorDetails,
    val placeDescription: String?,
    val lon: Double,
    val lat: Double,
    val dateInfo: String,
)

data class CreatorDetails(
    val id: String,
    val firstName: String,
    val avatarPath: String? = null
)






