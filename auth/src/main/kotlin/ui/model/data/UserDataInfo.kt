package ui.model.data

import android.net.Uri

data class UserDataInfo(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val description: String = "",
    val contacts: List<ContactType> = listOf(),
    val uri: Uri? = null
) {
    data class ContactType(
        val contactType: Int? = null,
        val url: String
    )

    override fun toString(): String {
        return "UserDataInfo(email='$email', password='$password', name='$name', description='$description', contacts=$contacts)"
    }



}

fun UserDataInfo.getContact(type: Int): String {
    return contacts.firstOrNull { it.contactType == type }?.url ?: ""
}