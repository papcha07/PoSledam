package domain.repository

import androidx.paging.PagingData
import domain.model.StreetAnimalParams
import domain.models.AdvertInfo
import domain.models.StreetDetails
import domain.models.StreetPetPreviewModel
import kotlinx.coroutines.flow.Flow

interface StreetRepository {
    fun getStreetAnimals(streetParams: StreetAnimalParams): Flow<PagingData<StreetPetPreviewModel>>
    suspend fun createStreetAdvert(advertInfo: AdvertInfo): Int

    suspend fun getInformationAboutStreetAnimal(id: String) : Pair<StreetDetails?, Int?>

}