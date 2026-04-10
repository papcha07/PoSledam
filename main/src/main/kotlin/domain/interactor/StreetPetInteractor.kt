package domain.interactor

import domain.models.AdvertInfo
import domain.models.StreetPetPreviewModel
import kotlinx.coroutines.flow.Flow

interface StreetPetInteractor {
    suspend fun getStreetAnimals(): Flow<Pair<List<StreetPetPreviewModel>?, Int?>>
    suspend fun createStreetAdvert(advertInfo: AdvertInfo) : Int

}