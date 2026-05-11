package domain.models

data class StreetDetails(
    val street: String,
    val house: String,
    val imagePath: String? = null,
    val creator: Creator,
    val placeDescription: String,
    val lon: Double,
    val lat: Double,
    val dateInfo: DateInfo,
)

data class DateInfo(
    val time: String,
    val date: String
)

data class Creator(
    val id: String,
    val firstName: String,
    val avatarPath: String? = null
)



