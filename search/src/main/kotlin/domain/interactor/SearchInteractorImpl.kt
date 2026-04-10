package domain.interactor

import domain.models.FoundPetInfo
import domain.models.PetUiPreview
import domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import model.InternetStatus
import ui.models.FilterDto

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

}