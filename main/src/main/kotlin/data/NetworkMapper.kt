package data

import apiService.models.street_models.StreetAnimalDetailsResponse
import domain.models.Creator
import domain.models.StreetDetails
import ui.other.timeUtils.DateTimeUtils

fun StreetAnimalDetailsResponse.toStreetDetails(): StreetDetails {
    return StreetDetails(
        street = this.street,
        house = this.house,
        imagePath = this.imagesPaths,
        creator = Creator(
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


