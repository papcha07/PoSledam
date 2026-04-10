package ui.models

data class FilterDto(
    val district: String? = null,
    val time: TimeFilter? = null,
    val typeOfPet: Int? = null,
    val gender: Int? = null,
    /** ISO 8601 дата последнего объявления (для пагинации) */
    val lastDateTime: String? = null,
)