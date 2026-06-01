package domain.interactor

import domain.models.FoundPetInfo
import domain.repository.SearchRepository
import model.InternetStatus
import ui.model.Response
import ui.viewModel.SpottedAnimalData

class SearchInteractorImpl(
    private val repository: SearchRepository
) : SearchInteractor {

    override suspend fun getInfoAboutPet(
        id: String,
        announcementType: Int
    ): Pair<FoundPetInfo?, InternetStatus?> {
        return repository.getInfoAboutPet(id, announcementType)
    }

    override suspend fun reportFoundAnimal(id: String): Response {
        return repository.reportFoundAnimal(id)
    }

    override suspend fun reportSpottedAnimal(
        id: String,
        spottedAnimalData: SpottedAnimalData
    ): Response {
        return repository.reportSpottedAnimal(id, spottedAnimalData)
    }

}