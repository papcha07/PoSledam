package data.repository

import AnnouncementType
import ApiResponse
import SendResult
import android.os.Build
import androidx.annotation.RequiresApi
import apiService.AnnouncementService
import apiService.models.announcement_models.UserPetInfoResponse
import domain.model.AnnouncementInfo
import domain.model.AnnouncementStatus
import domain.model.toAnnouncementRequest
import domain.repository.AnnouncementRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.InternetStatus
import ui.model.PetUiPreview
import ui.other.Converter

class AnnouncementRepositoryImpl(
    private val apiService: AnnouncementService,
    private val converter: Converter
) : AnnouncementRepository {


    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun sendAnnouncement(
        announcementInfo: AnnouncementInfo,
        files: List<String>,
        type: Int
    ): AnnouncementStatus {
        val requestType = if (type == 0) AnnouncementType.Miss else AnnouncementType.Found

        return when (val response = apiService.sendAnnouncement(
            announcementInfo.toAnnouncementRequest(),
            files = files.map { converter.convertToFile(it) },
            type = requestType
        )) {
            is SendResult.BadRequest -> AnnouncementStatus.Failed(InternetStatus.Error)
            is SendResult.Error -> AnnouncementStatus.Failed(InternetStatus.NoInternet)
            SendResult.Success -> AnnouncementStatus.Success
        }
    }

    override suspend fun getUserAnnouncements(type: Int): Pair<List<PetUiPreview>?, Int?> =
        withContext(Dispatchers.IO) {
            val requestType = if (type == 0) AnnouncementType.Miss else AnnouncementType.Found
            val response = apiService.getUserPets(requestType)
            when (response) {
                is ApiResponse.Error -> return@withContext Pair(null, response.errorCode)
                is ApiResponse.Success<List<UserPetInfoResponse>> -> {
                    val petUiPreviewAnimalList = response.data.map {
                        mapToPetUiPreview(it)
                    }
                    return@withContext Pair(petUiPreviewAnimalList, null)
                }
            }
        }


    private fun mapToPetUiPreview(userPetResponse: UserPetInfoResponse): PetUiPreview {
        return PetUiPreview(
            id = userPetResponse.id,
            breed = userPetResponse.breed ?: "Порода не указана",
            description = userPetResponse.description ?: "Нет описания",
            district = userPetResponse.district,
            imageUrl = userPetResponse.mainImagePath
        )
    }


}