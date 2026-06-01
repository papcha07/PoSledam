package data

import AnnouncementType
import ApiResponse
import SendResult
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import apiService.AnnouncementService
import data.SearchAnimalPagingSource.Companion.PAGE_SIZE
import domain.models.Creator
import domain.models.DateInfo
import domain.models.FilterDto
import domain.models.FoundPetInfo
import domain.models.PetInfo
import domain.models.PetUiPreview
import domain.repository.SearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import model.InternetStatus
import model.announcement.FoundPetRequest
import model.announcement.FoundPetResponse
import model.announcement.Location
import ui.model.Response
import ui.other.Converter
import ui.viewModel.SpottedAnimalData
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class SearchRepositoryImpl(
    private val announcementService: AnnouncementService,
    private val converter: Converter
) : SearchRepository {

    override suspend fun loadMissAnnouncementPage(filterDto: FilterDto): Flow<PagingData<PetUiPreview>> {
        val request = filterDto.toMissAllRequest()
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SearchAnimalPagingSource(
                    announcementService = announcementService,
                    filter = request,
                    type = SearchAnimalType.Missing
                )
            }
        ).flow
    }

    override suspend fun loadFindAnnouncementPage(filterDto: FilterDto): Flow<PagingData<PetUiPreview>> {
        val request = filterDto.toMissAllRequest()
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SearchAnimalPagingSource(
                    announcementService = announcementService,
                    filter = request,
                    type = SearchAnimalType.Found
                )
            }
        ).flow
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

    override suspend fun reportSpottedAnimal(
        id: String,
        spottedAnimalData: SpottedAnimalData
    ): Response {
        val convertedFiles = spottedAnimalData.uri.map {
            converter.convertToFile(it.toString())
        }
        val request = announcementService.reportSpottedAnimal(
            id,
            reportRequest = Location(spottedAnimalData.lat!!, spottedAnimalData.lon!!),
            files = convertedFiles,
        )
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