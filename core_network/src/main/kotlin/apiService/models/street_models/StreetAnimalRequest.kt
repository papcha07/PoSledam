package apiService.models.street_models

data class StreetAnimalRequest(
    val petType: Int,
    val lat: Double,
    val lon: Double,
    val eventDate: String,
    val placeDescription: String
)