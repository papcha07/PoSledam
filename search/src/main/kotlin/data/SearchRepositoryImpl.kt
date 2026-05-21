package data

import AnnouncementType
import ApiResponse
import SendResult
import android.os.Build
import androidx.annotation.RequiresApi
import apiService.AnnouncementService
import domain.models.Creator
import domain.models.DateInfo
import domain.models.FoundPetInfo
import domain.models.PetInfo
import domain.models.PetUiPreview
import domain.repository.SearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import model.InternetStatus
import model.announcement.FoundPetRequest
import model.announcement.FoundPetResponse
import model.announcement.MissAllDto
import model.announcement.MissAllDtoFound
import model.announcement.MissAllRequest
import ui.model.Response
import ui.models.FilterDto
import ui.models.toInstant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class SearchRepositoryImpl(
    private val announcementService: AnnouncementService
) : SearchRepository {


    @RequiresApi(Build.VERSION_CODES.O)

    override suspend fun findMissingAnnouncement(filterDto: FilterDto): Flow<Pair<List<PetUiPreview>?, InternetStatus?>> =
        flow {
            val allMissingResult = announcementService.findMissingAnnouncement(
                missAllInfo = convertToMissRequest(filterDto)
            )
            when (allMissingResult) {

                is ApiResponse.Error -> {
                    when (allMissingResult.errorCode) {

                        500 -> {
                            emit(Pair(null, InternetStatus.Error))
                        }

                        400 -> {
                            emit(Pair(null, InternetStatus.Error))
                        }
                    }
                }

                is ApiResponse.Success<List<MissAllDto>> -> {
                    val previewList: MutableList<PetUiPreview> = mutableListOf()
                    allMissingResult.data.map { dto ->
                        previewList.add(convertToPetPreview(dto))
                    }
                    emit(Pair(previewList, null))
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun findFoundAnnouncement(filterDto: FilterDto): Flow<Pair<List<PetUiPreview>?, InternetStatus?>> =

        flow {
            val allMissingResult = announcementService.findFoundAnnouncement(
                missAllInfo = convertToMissRequest(filterDto)
            )
            when (allMissingResult) {

                is ApiResponse.Error -> {
                    when (allMissingResult.errorCode) {

                        500 -> {
                            emit(Pair(null, InternetStatus.Error))
                        }

                        400 -> {
                            emit(Pair(null, InternetStatus.Error))
                        }
                    }
                }

                is ApiResponse.Success<List<MissAllDtoFound>> -> {
                    val previewList: MutableList<PetUiPreview> = mutableListOf()
                    allMissingResult.data.map { dto ->
                        previewList.add(convertToPetPreviewFound(dto))
                    }
                    println("$  found {previewList.toString()}")
                    emit(Pair(previewList, null))
                }
            }

        }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getInfoAboutPet(
        id: String,
        announcementType: Int
    ): Pair<FoundPetInfo?, InternetStatus?> =
        withContext(Dispatchers.IO) {

            val type =
                if (announcementType == 1) AnnouncementType.Miss
                else AnnouncementType.Found

            val response =
                announcementService.getInfoAboutPet(
                    FoundPetRequest(id = id),
                    type
                )

            when (response) {
                is ApiResponse.Error -> {
                    when (response.errorCode) {
                        400 -> Pair(null, InternetStatus.Error)
                        -1 -> Pair(null, InternetStatus.NoInternet)
                        else -> Pair(null, InternetStatus.Error)
                    }
                }

                is ApiResponse.Success<FoundPetResponse> -> {
                    val petInfo = convertToPetInfo(response.data)
                    Pair(petInfo, null)
                }
            }
        }

    override suspend fun reportFoundAnimal(id: String): Response {
        val request = announcementService.reportFoundAnimal(id)
        return when (request) {
            is SendResult.BadRequest -> Response.SERVER_ERROR
            is SendResult.Error -> Response.INTERNET_ERROR
            SendResult.Success -> Response.SUCCESS
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertToPetInfo(petResponse: FoundPetResponse): FoundPetInfo {
        return FoundPetInfo(
            street = petResponse.street,
            house = petResponse.house,
            district = petResponse.district,
            imagePath = petResponse.imagesPaths?.get(0),
            creator = Creator(
                id = petResponse.creator.id,
                firstName = petResponse.creator.firstName,
                avatarPath = petResponse.creator.avatarPath
            ),
            petInfo = PetInfo(
                petType = petResponse.petType,
                gender = petResponse.gender,
                color = petResponse.color,
                breed = petResponse.breed,
                description = petResponse.description
            ),
            lon = petResponse.location.longitude,
            lat = petResponse.location.latitude,
            dateInfo = DateInfo(
                time = formatEventTime(petResponse.eventDate),
                date = formatEventDate(petResponse.eventDate)
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertToMissRequest(filterDto: FilterDto): MissAllRequest {
        val lastInstant = filterDto.lastDateTime?.let { java.time.Instant.parse(it) }
        return MissAllRequest(
            lastDateTime = lastInstant,
            district = filterDto.district?.takeIf { it.isNotBlank() },
            from = filterDto.time?.toInstant(),
            type = filterDto.typeOfPet,
            gender = filterDto.gender
        )
    }

    private fun convertToPetPreview(missAllDto: MissAllDto): PetUiPreview {
        return PetUiPreview(
            id = missAllDto.id,
            petName = missAllDto.petName,
            description = missAllDto.description ?: "Нет описания",
            district = missAllDto.district,
            imageUrl = missAllDto.mainImagePath,
            createdAt = missAllDto.createdAt ?: missAllDto.eventDate
        )
    }

    private fun convertToPetPreviewFound(missAllDto: MissAllDtoFound): PetUiPreview {
        return PetUiPreview(
            id = missAllDto.id,
            description = missAllDto.description ?: "Нет описания",
            district = missAllDto.district,
            imageUrl = missAllDto.mainImagePath,
            breed = missAllDto.breed,
            createdAt = missAllDto.createdAt ?: missAllDto.eventDate
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatEventDate(dateTimeString: String): String {
        val parsed = OffsetDateTime.parse(dateTimeString)
        val systemZone = ZoneId.systemDefault()
        val localDate = parsed.atZoneSameInstant(systemZone).toLocalDate()
        val formatter = DateTimeFormatter.ofPattern("dd/MM")
            .withLocale(Locale.getDefault())
        return localDate.format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatEventTime(dateTimeString: String): String {
        val parsed = OffsetDateTime.parse(dateTimeString)
        val systemZone = ZoneId.systemDefault()
        val localTime = parsed.atZoneSameInstant(systemZone).toLocalTime()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
            .withLocale(Locale.getDefault())
        return localTime.format(formatter)
    }


}