package data

import android.os.Build
import androidx.annotation.RequiresApi
import apiService.models.street_models.StreetAnimalDetailsResponse
import domain.models.CreatorDetails
import domain.models.StreetDetails
import ui.other.timeUtils.DateTimeUtils

@RequiresApi(Build.VERSION_CODES.O)
fun StreetAnimalDetailsResponse.toStreetDetails(): StreetDetails {
    return StreetDetails(
        imagePath = this.imagePaths,
        creator = CreatorDetails(
            id = this.creator.id,
            firstName = this.creator.firstName,
            avatarPath = this.creator.avatarPath
        ),
        placeDescription = this.placeDescription,
        lon = this.location.longitude,
        lat = this.location.latitude,
        dateInfo = DateTimeUtils.formatUtcToDeviceTime(this.eventDate)
    )
}


