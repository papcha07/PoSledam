package ui.models

import androidx.annotation.DrawableRes

data class PetUiInfo(
    val title: String,
    val description: String,
    val address: String,
    @DrawableRes val image: Int,
    val type: Int
)