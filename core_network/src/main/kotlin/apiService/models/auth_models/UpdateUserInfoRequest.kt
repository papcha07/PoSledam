package apiService.models.auth_models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import model.auth.response.Contact

@Serializable
data class UpdateUserInfoRequest(
    val id : String,
    val firstName: String,
    val description: String? = null,
    @SerialName("userContacts")
    val contacts: List<Contact>? = null
)
