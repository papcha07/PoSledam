package domain.user

import android.net.Uri

data class User(
    val name: String,
    val description: String,
    val avatarPath: Uri?,
    val tg: String?,
    val wh: String?,
    val vk: String?,
)