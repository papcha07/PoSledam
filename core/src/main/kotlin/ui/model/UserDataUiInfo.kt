package ui.model

import android.net.Uri

data class UserDataUiInfo(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val contacts: List<ContactType> = listOf(),
    val uri: String? = null
) {
    data class ContactType(
        val contactType: Int? = null,
        val url: String
    )

    override fun toString(): String {
        return "UserDataInfo(id = $id name='$name', description='$description', contacts=$contacts)"
    }

}

fun UserDataUiInfo.getContact(type: Int): String {
    return contacts.firstOrNull { it.contactType == type }?.url ?: ""
}