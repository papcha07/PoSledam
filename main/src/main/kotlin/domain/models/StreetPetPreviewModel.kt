package domain.models

data class StreetPetPreviewModel(
    val id: String,
    val street: String?,
    val district: String?,
    val time: String,
    val date: String,
    val image: String,
    val minutesAgo: Long,
)
