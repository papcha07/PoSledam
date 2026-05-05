package data.repository

import ApiResponse
import android.os.Build
import androidx.annotation.RequiresApi
import apiService.StreetService
import apiService.models.street_models.StreetAnimalRequest
import apiService.models.street_models.StreetAnimalResponse
import domain.models.AdvertInfo
import domain.models.StreetPetPreviewModel
import domain.repository.StreetRepository
import ui.other.Converter
import ui.other.timeUtils.DateTimeUtils
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class StreetRepositoryImpl(
    private val streetService: StreetService,
    private val converter: Converter,
) : StreetRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getStreetAnimals(): Pair<List<StreetPetPreviewModel>?, Int?> {

        val response = streetService.getStreetAnimals()
        return when (response) {
            is ApiResponse.Error -> {
                Pair(null, response.errorCode)
            }

            is ApiResponse.Success<List<StreetAnimalResponse>> -> {
                val animalList = response.data.map {
                    convertToStreetPreviewModel(it)
                }
                Pair(animalList, null)
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

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertToUiTime(timeFromServer: String): Pair<String, String> {
        val instant = Instant.parse(timeFromServer)
        val dateTime = instant.atZone(ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        val formatted = dateTime.format(formatter).split(" ")
        return Pair(formatted[0], formatted[1].replace(":", "/"))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun minutesAgoSafe(timeFromServer: String): Long {
        val posted = Instant.parse(timeFromServer)
        val now = Instant.now()
        return Duration.between(posted, now).toMinutes()
    }


}