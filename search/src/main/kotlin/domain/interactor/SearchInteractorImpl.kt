package domain.interactor

import domain.models.FoundPetInfo
import domain.models.PetUiPreview
import domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import model.InternetStatus
import ui.model.Response
import ui.models.FilterDto
import ui.viewModel.SpottedAnimalData

class SearchInteractorImpl(
    private val repository: SearchRepository
) : SearchInteractor {
    override suspend fun findMissingAnnouncement(filterDto: FilterDto): Flow<Pair<List<PetUiPreview>?, InternetStatus?>> {
        return repository.findMissingAnnouncement(filterDto)
    }

    override suspend fun findFoundAnnouncement(filterDto: FilterDto): Flow<Pair<List<PetUiPreview>?, InternetStatus?>> {
        return repository.findFoundAnnouncement(filterDto)
    }

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