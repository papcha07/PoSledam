package apiService.models.auth_models

import kotlinx.serialization.Serializable

@Serializable
data class DeviceTokenRequest(
    val deviceToken: String
)