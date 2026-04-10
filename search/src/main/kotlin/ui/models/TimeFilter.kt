package ui.models

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Clock
import java.time.Instant
import java.time.ZonedDateTime

enum class TimeFilter {
    TODAY,
    YESTERDAY,
    THREE_DAYS,
    WEEK,
    MONTH
}

@RequiresApi(Build.VERSION_CODES.O)
fun TimeFilter.toInstant(clock: Clock = Clock.systemDefaultZone()): Instant {
    val now = ZonedDateTime.now(clock)
    return when (this) {
        TimeFilter.TODAY -> now.toLocalDate()
            .atStartOfDay(now.zone)
            .toInstant()

        TimeFilter.YESTERDAY -> now.minusDays(1)
            .toLocalDate()
            .atStartOfDay(now.zone)
            .toInstant()

        TimeFilter.THREE_DAYS -> now.minusDays(3).toInstant()
        TimeFilter.WEEK -> now.minusWeeks(1).toInstant()
        TimeFilter.MONTH -> now.minusMonths(1).toInstant()
    }
}
