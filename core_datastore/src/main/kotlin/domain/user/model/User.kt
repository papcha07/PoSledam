package domain.user.model

import android.net.Uri

data class User(
    val id: String,
    val name: String,
    val description: String?,
    val avatarPath: Uri?,
    val tg: String? = null,
    val wh: String? = null,
    val vk: String? = null,
)