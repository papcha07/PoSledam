package ui.model

import androidx.annotation.DrawableRes

data class PetInfo(
    @DrawableRes
    val image: Int,
    val petCategory: PetCategory,
    val name: String,
    val isFound: Boolean,
    val address: String,
    val date: String,
    val time: String
) {
    enum class PetCategory {
        Dog,
        Cat
    }
}