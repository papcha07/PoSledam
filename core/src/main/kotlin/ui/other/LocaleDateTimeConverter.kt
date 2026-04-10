package ui.other

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object LocaleDateTimeConverter {

    @RequiresApi(Build.VERSION_CODES.O)
    fun toUtcRfc3339(
        date: LocalDate,
        time: LocalTime,
        zone: ZoneId = ZoneId.systemDefault()
    ): String {
        val localDateTime = LocalDateTime.of(date, time)
        val instant = localDateTime
            .atZone(zone)
            .toInstant()
        return DateTimeFormatter.ISO_INSTANT.format(instant)
    }



}