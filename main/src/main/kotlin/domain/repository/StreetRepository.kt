package domain.repository

import domain.models.AdvertInfo
import domain.models.StreetPetPreviewModel

interface StreetRepository {
    suspend fun getStreetAnimals(): Pair<List<StreetPetPreviewModel>?, Int?>
    suspend fun createStreetAdvert(advertInfo: AdvertInfo): Int

}