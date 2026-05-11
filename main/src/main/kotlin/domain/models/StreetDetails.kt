package domain.models

data class StreetDetails(
    val street: String,
    val house: String,
    val imagePath: List<String>,
    val creator: Creator,
    val placeDescription: String,
    val lon: Double,
    val lat: Double,
    val dateInfo: String,
)

data class Creator(
    val id: String,
    val firstName: String,
    val avatarPath: String? = null
)






