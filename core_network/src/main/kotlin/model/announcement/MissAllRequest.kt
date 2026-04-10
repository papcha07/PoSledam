package model.announcement

import java.time.Instant

data class MissAllRequest(
    val lastDateTime: Instant? = null,
    val district: String? = null,
    val from: Instant? = null,
    val type: Int? = null,
    val gender: Int? = null
)
