package apiService.models.announcement_models

import kotlinx.serialization.Serializable

@Serializable
data class UserPetInfoResponse(
    val id: String,
    val breed: String?,
    val district: String?,
    val description: String?,
    val createdAt: String,
    val mainImagePath: String
)