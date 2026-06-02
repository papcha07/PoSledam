package domain.models

data class PetUiPreview(
    val id: String,
    val petName: String? = null,
    val description: String? = null,
    val district: String?,
    val imageUrl: String?,
    val breed: String? = null,
    /** ISO 8601 дата для пагинации (createdAt или eventDate) */
    val createdAt: String? = null
)
