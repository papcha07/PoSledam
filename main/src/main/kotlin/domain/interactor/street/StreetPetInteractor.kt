package domain.interactor.street

import androidx.paging.PagingData
import domain.models.AdvertInfo
import domain.models.StreetDetails
import domain.models.StreetPetPreviewModel
import kotlinx.coroutines.flow.Flow

interface StreetPetInteractor {
    fun getStreetAnimals(): Flow<PagingData<StreetPetPreviewModel>>
    suspend fun createStreetAdvert(advertInfo: AdvertInfo): Int
    suspend fun getInfoAboutStreetAnimal(id: String): Pair<StreetDetails?, Int?>


}