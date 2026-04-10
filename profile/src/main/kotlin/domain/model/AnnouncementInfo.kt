package domain.model

import android.os.Build
import androidx.annotation.RequiresApi
import model.announcement.AnnouncementRequest
import ui.other.LocaleDateTimeConverter
import java.time.LocalDate
import java.time.LocalTime

data class AnnouncementInfo(
    val location: Location,
    val petType: Int,
    val gender: Int,
    val color: String? = null,
    val breed: String? = null,
    val petName: String,
    val eventDate: LocalDate,
    val time: LocalTime,
    val description: String? = null,
)

data class Location(
    val latitude: Double,
    val longitude: Double
)

@RequiresApi(Build.VERSION_CODES.O)
fun AnnouncementInfo.toAnnouncementRequest(): AnnouncementRequest {
    return AnnouncementRequest(
        location = model.announcement.Location(
            latitude = location.latitude,
            longitude = location.longitude
        ),
        petType = petType,
        gender = gender,
        color = color,
        breed = breed,
        petName = petName,
        eventDate = LocaleDateTimeConverter.toUtcRfc3339(date = eventDate, time = time),
        description = description
    )
}


