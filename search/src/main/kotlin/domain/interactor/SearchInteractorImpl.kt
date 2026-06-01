package domain.interactor

import androidx.paging.PagingData
import domain.models.FilterDto
import domain.models.FoundPetInfo
import domain.models.PetUiPreview
import domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import model.InternetStatus
import ui.model.Response
import ui.viewModel.SpottedAnimalData

class SearchInteractorImpl(
    private val repository: SearchRepository
) : SearchInteractor {
    override suspend fun loadMissAnnouncementPage(filterDto: FilterDto): Flow<PagingData<PetUiPreview>> {
        return repository.loadMissAnnouncementPage(filterDto)
    }

    override suspend fun loadFindAnnouncementPage(filterDto: FilterDto): Flow<PagingData<PetUiPreview>> {
        return repository.loadFindAnnouncementPage(filterDto)
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