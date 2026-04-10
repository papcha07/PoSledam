package ui.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

data class NotificationInfo(
    val title: String,
    val message: String? = null,
    @DrawableRes
    val image: Int? = null,
    val status: Color,
    val date: String
)
