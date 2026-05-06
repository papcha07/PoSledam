package domain.interactor

import androidx.paging.PagingData
import domain.models.AdvertInfo
import domain.models.StreetPetPreviewModel
import domain.repository.StreetRepository
import kotlinx.coroutines.flow.Flow

class StreetPetInteractorImpl(
    private val streetRepository: StreetRepository
) : StreetPetInteractor {

    override fun getStreetAnimals(): Flow<PagingData<StreetPetPreviewModel>> {
        return streetRepository.getStreetAnimals()
    }

    override suspend fun createStreetAdvert(advertInfo: AdvertInfo): Int {
        return streetRepository.createStreetAdvert(advertInfo)
    }
}