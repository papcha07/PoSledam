package domain.interactor.street

import androidx.paging.PagingData
import domain.model.StreetAnimalParams
import domain.models.AdvertInfo
import domain.models.StreetDetails
import domain.models.StreetPetPreviewModel
import domain.repository.StreetRepository
import kotlinx.coroutines.flow.Flow

class StreetPetInteractorImpl(
    private val streetRepository: StreetRepository
) : StreetPetInteractor {

    override fun getStreetAnimals(streetAnimalParams: StreetAnimalParams): Flow<PagingData<StreetPetPreviewModel>> {
        return streetRepository.getStreetAnimals(streetParams = streetAnimalParams)
    }

    override suspend fun createStreetAdvert(advertInfo: AdvertInfo): Int {
        return streetRepository.createStreetAdvert(advertInfo)
    }

    override suspend fun getInfoAboutStreetAnimal(id: String): Pair<StreetDetails?, Int?> {
        return streetRepository.getInformationAboutStreetAnimal(id)
    }
}