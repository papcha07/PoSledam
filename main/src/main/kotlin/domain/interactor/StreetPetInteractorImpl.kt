package domain.interactor

import domain.models.AdvertInfo
import domain.models.StreetPetPreviewModel
import domain.repository.StreetRepository
import kotlinx.coroutines.flow.Flow

class StreetPetInteractorImpl(
    private val streetRepository: StreetRepository
) : StreetPetInteractor {
    override suspend fun getStreetAnimals(): Flow<Pair<List<StreetPetPreviewModel>?, Int?>> {
        return streetRepository.getStreetAnimals()
    }

    override suspend fun createStreetAdvert(advertInfo: AdvertInfo): Int {
        return streetRepository.createStreetAdvert(advertInfo)
    }
}