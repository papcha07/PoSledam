package data

import domain.models.FilterDto
import domain.models.PetUiPreview
import model.announcement.MissAllDto
import model.announcement.MissAllRequest

fun FilterDto.toMissAllRequest(): MissAllRequest {
    return MissAllRequest(
        lastDateTime = this.lastDateTime,
        district = this.district,
        type = this.typeOfPet,
        gender = this.gender,
        searchRadius = this.searchRadius,
        searchCenterLatitude = this.searchCenterLatitude,
        searchCenterLongitude = this.searchCenterLongitude
    )
}


fun MissAllDto.toPetUiPreview() : PetUiPreview{
    return PetUiPreview(
        id = this.id,
        petName = this.petName,
        description = this.description,
        district = this.district,
        imageUrl = this.mainImagePath,
        breed = this.breed,
        createdAt = this.createdAt
    )
}