package data.repository

import AnnouncementType
import ApiResponse
import SendResult
import android.os.Build
import androidx.annotation.RequiresApi
import apiService.AnnouncementService
import apiService.models.announcement_models.SpottedLocationResponse
import apiService.models.announcement_models.UserPetInfoResponse
import domain.model.AnnouncementInfo
import domain.model.AnnouncementStatus
import domain.model.ProfileAnnouncementDetails
import domain.model.SpottedLocation
import domain.model.toAnnouncementRequest
import domain.repository.AnnouncementRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.announcement.FoundPetRequest
import model.announcement.FoundPetResponse
import model.InternetStatus
import ui.model.PetUiPreview
import ui.other.Converter
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class AnnouncementRepositoryImpl(
    private val apiService: AnnouncementService,
    private val converter: Converter
) : AnnouncementRepository {
    override suspend fun cancelAnnouncement(cancelReason: CancelReason): Pair<Boolean, InternetStatus?> {
        val requestModel = cancelReason.toCancelAnnouncementRequest()
        val response = apiService.cancelMissAnnouncement(
            cancelAnnouncementRequest = requestModel,
            type = cancelReason.type.toMethodType()
        )

        return when (response) {
            is SendResult.BadRequest -> {
                Pair(false, InternetStatus.Error)
            }

            is SendResult.Error -> {
                Pair(false, InternetStatus.NoInternet)
            }

            SendResult.Success -> {
                Pair(true, InternetStatus.Error)
            }
        }
    }


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

    override suspend fun getAnnouncementDetails(
        id: String,
        type: Int
    ): Pair<ProfileAnnouncementDetails?, InternetStatus?> =
        withContext(Dispatchers.IO) {
            val requestType = if (type == 0) AnnouncementType.Miss else AnnouncementType.Found
            val response = apiService.getInfoAboutPet(
                foundRequest = FoundPetRequest(id),
                announcementType = requestType
            )

            when (response) {
                is ApiResponse.Error -> Pair(null, response.errorCode.toInternetStatus())
                is ApiResponse.Success<FoundPetResponse> -> {
                    Pair(response.data.mapToProfileAnnouncementDetails(), null)
                }
            }
        }

    override suspend fun getSpottedLocations(
        announcementId: String
    ): Pair<List<SpottedLocation>?, InternetStatus?> =
        withContext(Dispatchers.IO) {
            when (val response = apiService.getSpottedLocations(announcementId)) {
                is ApiResponse.Error -> Pair(null, response.errorCode.toInternetStatus())
                is ApiResponse.Success<List<SpottedLocationResponse>> -> {
                    Pair(response.data.map { it.mapToSpottedLocation() }, null)
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

    private fun FoundPetResponse.mapToProfileAnnouncementDetails(): ProfileAnnouncementDetails {
        val dateTime = formatDateTime(eventDate)

        return ProfileAnnouncementDetails(
            id = id,
            imagePath = imagesPaths?.firstOrNull(),
            petType = petType,
            gender = gender,
            color = color,
            breed = breed,
            description = description,
            district = district,
            street = street,
            house = house,
            latitude = location.latitude,
            longitude = location.longitude,
            eventDate = dateTime.first,
            eventTime = dateTime.second
        )
    }

    private fun SpottedLocationResponse.mapToSpottedLocation(): SpottedLocation {
        val dateTime = formatDateTime(createdAt)
        val userName = listOf(
            spottedUser.firstName,
            spottedUser.secondName
        )
            .filter { !it.isNullOrBlank() }
            .joinToString(separator = " ")
            .ifBlank { "Пользователь" }

        return SpottedLocation(
            id = id,
            spottedUserName = userName,
            createdDate = dateTime.first,
            createdTime = dateTime.second,
            latitude = location.latitude,
            longitude = location.longitude,
            imagesPath = imagesPath
        )
    }

    private fun Int.toInternetStatus(): InternetStatus {
        return when (this) {
            -1 -> InternetStatus.NoInternet
            else -> InternetStatus.Error
        }
    }

    private fun formatDateTime(dateTimeString: String): Pair<String, String> {
        return runCatching {
            val parsed = OffsetDateTime.parse(dateTimeString)
            val zonedDateTime = parsed.atZoneSameInstant(ZoneId.systemDefault())
            val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                .withLocale(Locale.getDefault())
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                .withLocale(Locale.getDefault())

            zonedDateTime.toLocalDate().format(dateFormatter) to
                    zonedDateTime.toLocalTime().format(timeFormatter)
        }.getOrElse {
            dateTimeString to ""
        }
    }

}
