package model.auth.response

import kotlinx.serialization.Serializable

@Serializable
data class Contact(
    val contactType: Int?,
    val url: String
)