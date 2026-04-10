package apiService.models.auth_models

import kotlinx.serialization.Serializable
import model.auth.response.Contact

@Serializable
data class UserInfoResponse(
    val id: String,
    val contacts: List<Contact>? = null,
    val firstName: String,
    val secondName: String? = null,
    val patronymic: String? = null,
    val avatarPath: String? = null,
    val description: String? = null
)


