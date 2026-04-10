package domain.repository

import domain.models.AdvertInfo
import domain.models.StreetPetPreviewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface StreetRepository {
    suspend fun getStreetAnimals(): Flow<Pair<List<StreetPetPreviewModel>?, Int?>>
    suspend fun createStreetAdvert(advertInfo: AdvertInfo) : Int

}