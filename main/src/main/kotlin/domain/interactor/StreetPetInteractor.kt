package domain.interactor

import domain.models.AdvertInfo
import domain.models.StreetPetPreviewModel

interface StreetPetInteractor {
    suspend fun getStreetAnimals(): Pair<List<StreetPetPreviewModel>?, Int?>
    suspend fun createStreetAdvert(advertInfo: AdvertInfo): Int

}