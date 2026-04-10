package ui.model

import androidx.annotation.DrawableRes

data class ProfileInfo(
    val id: Int,
    @DrawableRes
    val image: Int,
    val name: String,
    val description: String? = null
)