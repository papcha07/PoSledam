package ui.model

data class NearPetInfo(
    val text: String,
    val foundCategory : FoundCategory,
    val address: String,
    val time: String,
    val date: String
){
    enum class FoundCategory{
        FOUND,
        UNFOUND
    }
}