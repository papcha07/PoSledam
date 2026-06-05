package domain.model

data class ProfileAnnouncementDetails(
    val id: String,
    val imagePath: String?,
    val petType: Int,
    val gender: Int,
    val color: String,
    val breed: String,
    val description: String,
    val district: String?,
    val street: String?,
    val house: String?,
    val latitude: Double,
    val longitude: Double,
    val eventDate: String,
    val eventTime: String
)

data class SpottedLocation(
    val id: String,
    val spottedUserName: String,
    val createdDate: String,
    val createdTime: String,
    val latitude: Double,
    val longitude: Double,
    val imagesPath: List<String>
)
