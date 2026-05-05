package domain.interactor

import domain.models.AdvertInfo
import domain.models.StreetPetPreviewModel
import domain.repository.StreetRepository

class StreetPetInteractorImpl(
    private val streetRepository: StreetRepository
) : StreetPetInteractor {

    override suspend fun getStreetAnimals(): Pair<List<StreetPetPreviewModel>?, Int?> {
        return streetRepository.getStreetAnimals()
    }

    override suspend fun createStreetAdvert(advertInfo: AdvertInfo): Int {
        return streetRepository.createStreetAdvert(advertInfo)
    }
}