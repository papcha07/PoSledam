package domain.models

data class FoundPetInfo(
    val street: String?,
    val house: String?,
    val district: String? = null,
    val imagePath: String? = null,
    val creator: Creator,
    val petInfo: PetInfo,
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
    val avatarPath: String? = null,
    val description: String? = null,
    val tg: String? = null,
    val wh: String? = null,
    val vk: String? = null
)

data class PetInfo(
    val petType: Int,
    val gender: Int,
    val color: String,
    val breed: String,
    val description: String
)

