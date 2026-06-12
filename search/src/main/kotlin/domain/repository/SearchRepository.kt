package domain.repository

import android.net.Uri
import androidx.paging.PagingData
import domain.models.FilterDto
import domain.models.FoundPetInfo
import domain.models.PetUiPreview
import kotlinx.coroutines.flow.Flow
import model.InternetStatus
import ui.model.Response
import ui.viewModel.SpottedAnimalData

interface SearchRepository {

    suspend fun loadMissAnnouncementPage(filterDto: FilterDto): Flow<PagingData<PetUiPreview>>
    suspend fun loadFindAnnouncementPage(filterDto: FilterDto): Flow<PagingData<PetUiPreview>>
    suspend fun getInfoAboutPet(
        id: String,
        announcementType: Int
    ): Pair<FoundPetInfo?, InternetStatus?>

    suspend fun reportFoundAnimal(id: String, uris: List<Uri>): Response
    suspend fun reportSpottedAnimal(id: String, spottedAnimalData: SpottedAnimalData): Response

}