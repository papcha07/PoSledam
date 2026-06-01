package domain.repository

import androidx.paging.PagingData
import domain.models.FilterDto
import domain.models.FoundPetInfo
import domain.models.PetUiPreview
import kotlinx.coroutines.flow.Flow
import model.InternetStatus
import ui.model.Response
import ui.viewModel.SpottedAnimalData

interface SearchRepository {
    suspend fun getInfoAboutPet(
        id: String,
        announcementType: Int
    ): Pair<FoundPetInfo?, InternetStatus?>

    suspend fun reportFoundAnimal(id: String): Response
    suspend fun reportSpottedAnimal(id: String, spottedAnimalData: SpottedAnimalData): Response
}