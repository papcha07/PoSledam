package domain.interactor

import domain.models.FoundPetInfo
import model.InternetStatus
import ui.model.Response
import ui.viewModel.SpottedAnimalData

interface SearchInteractor {



    suspend fun getInfoAboutPet(
        id: String,
        announcementType: Int
    ): Pair<FoundPetInfo?, InternetStatus?>

    suspend fun reportFoundAnimal(id: String): Response
    suspend fun reportSpottedAnimal(id: String, spottedAnimalData: SpottedAnimalData): Response
}