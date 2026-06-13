package apiService.models.announcement_models

import kotlinx.serialization.Serializable

@Serializable
data class FoundReportResponse(
    val id: String,
    val spottedUser: FoundReportUserResponse,
    val createdAt: String,
    val imagesPath: List<String> = emptyList()
)

@Serializable
data class FoundReportUserResponse(
    val id: String,
    val firstName: String,
    val secondName: String? = null,
    val contacts: List<FoundReportContactResponse>? = null
)

@Serializable
data class FoundReportContactResponse(
    val contactType: Int? = null,
    val url: String? = null
)
