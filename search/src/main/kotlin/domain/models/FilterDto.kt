package domain.models

import ui.models.TimeFilter

data class FilterDto(
    val district: String? = null,
    val time: TimeFilter? = null,
    val typeOfPet: Int? = null,
    val gender: Int? = null,
    /** ISO 8601 дата последнего объявления (для пагинации) */
    val lastDateTime: String? = null,
    val searchRadius: Int? = null,
    val searchCenterLatitude: Double? = null,
    val searchCenterLongitude: Double? = null
)