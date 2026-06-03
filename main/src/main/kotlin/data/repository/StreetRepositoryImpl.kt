package data.repository

import ApiResponse
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import apiService.StreetService
import apiService.models.street_models.StreetAnimalDetailsResponse
import apiService.models.street_models.StreetAnimalRequest
import apiService.models.street_models.StreetAnimalResponse
import data.pager.StreetAnimalPagingSource
import data.toStreetDetails
import domain.model.StreetAnimalParams
import domain.models.AdvertInfo
import domain.models.StreetDetails
import domain.models.StreetPetPreviewModel
import domain.repository.StreetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ui.other.Converter
import ui.other.timeUtils.DateTimeUtils
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class StreetRepositoryImpl(
    private val streetService: StreetService,
    private val converter: Converter,
) : StreetRepository {

    override fun getStreetAnimals(streetParams: StreetAnimalParams): Flow<PagingData<StreetPetPreviewModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                StreetAnimalPagingSource(
                    service = streetService,
                    streetParams = streetParams,
                )
            }
        ).flow.map { pagingData ->
            pagingData.map { response ->
                convertToStreetPreviewModel(response)
            }
        }
    }


    override suspend fun createStreetAdvert(advertInfo: AdvertInfo): Int {
        val files = advertInfo.images.map {
            converter.convertToFile(it.toString())
        }

        val currentDate = DateTimeUtils.getUtcFromDevice()
        val response = streetService.createStreetAnimal(
            streetAnimalRequest = StreetAnimalRequest(
                petType = 2,
                lat = advertInfo.lat,
                lon = advertInfo.lon,
                eventDate = currentDate,
                placeDescription = advertInfo.placeDescription
            ),
            fileList = files
        )
        return response
    }

    override suspend fun getInformationAboutStreetAnimal(id: String): Pair<StreetDetails?, Int?> {
        val response = streetService.getDetailsAboutStreetAnimal(id)
        return when (response) {
            is ApiResponse.Error -> {
                Pair(null, response.errorCode)
            }

            is ApiResponse.Success<StreetAnimalDetailsResponse> -> {
                val streetAnimalResponse = response.data
                val streetDetails = streetAnimalResponse.toStreetDetails()
                Pair(streetDetails, null)
            }
        }
    }

    private fun convertToStreetPreviewModel(streetResponse: StreetAnimalResponse): StreetPetPreviewModel {
        val primeTime = convertToUiTime(streetResponse.eventDate)
        return StreetPetPreviewModel(
            id = streetResponse.id,
            street = streetResponse.street,
            district = streetResponse.district,
            time = primeTime.second,
            date = primeTime.first,
            image = streetResponse.mainImagePath,
            minutesAgo = minutesAgoSafe(timeFromServer = streetResponse.eventDate)
        )
    }

    private fun convertToUiTime(timeFromServer: String): Pair<String, String> {
        val instant = Instant.parse(timeFromServer)
        val dateTime = instant.atZone(ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        val formatted = dateTime.format(formatter).split(" ")
        return Pair(formatted[0], formatted[1].replace(":", "/"))
    }

    fun minutesAgoSafe(timeFromServer: String): Long {
        val posted = Instant.parse(timeFromServer)
        val now = Instant.now()
        return Duration.between(posted, now).toMinutes()
    }
}