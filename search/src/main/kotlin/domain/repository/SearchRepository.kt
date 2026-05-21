package domain.repository

import domain.models.FoundPetInfo
import domain.models.PetUiPreview
import kotlinx.coroutines.flow.Flow
import model.InternetStatus
import ui.model.Response
import ui.models.FilterDto

interface SearchRepository {
    suspend fun findMissingAnnouncement(filterDto: FilterDto): Flow<Pair<List<PetUiPreview>?, InternetStatus?>>
    suspend fun findFoundAnnouncement(filterDto: FilterDto): Flow<Pair<List<PetUiPreview>?, InternetStatus?>>
    suspend fun getInfoAboutPet(
        id: String,
        announcementType: Int
    ): Pair<FoundPetInfo?, InternetStatus?>

    suspend fun reportFoundAnimal(id: String): Response
}