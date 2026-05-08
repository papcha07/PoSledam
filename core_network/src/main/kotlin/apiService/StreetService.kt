package apiService

import ApiResponse
import android.util.Log
import apiService.models.street_models.StreetAnimalRequest
import apiService.models.street_models.StreetAnimalResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class StreetService(private val client: HttpClient) {

    suspend fun getStreetAnimals(streetRequest: StreetListRequest): ApiResponse<List<StreetAnimalResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = client.get("api/street-pet-announcement/feed") {
                    streetRequest.lastDateTime?.let {
                        parameter("lastDateTime", it)
                    }
                    streetRequest.from?.let {
                        parameter("from", it)
                    }
                    streetRequest.type?.let {
                        parameter("type", it.toString())
                    }
                }
                if (response.status.isSuccess()) {
                    val body = response.body<List<StreetAnimalResponse>>()
                    ApiResponse.Success(body)
                } else ApiResponse.Error(400)
            } catch (e: Exception) {
                ApiResponse.Error(-1)
            }
        }
    }

    suspend fun createStreetAnimal(
        streetAnimalRequest: StreetAnimalRequest,
        fileList: List<File>
    ): Int {
        return withContext(Dispatchers.IO) {
            try {
                val response = client.submitFormWithBinaryData(
                    url = "api/street-pet-announcement",
                    formData = formData {
                        append("petType", streetAnimalRequest.petType)
                        append("Location.Latitude", streetAnimalRequest.lat)
                        append("Location.Longitude", streetAnimalRequest.lon)
                        append("eventDate", streetAnimalRequest.eventDate)
                        append("placeDescription", streetAnimalRequest.placeDescription)
                        fileList.forEach { file ->
                            append(
                                key = "Images",
                                value = file.readBytes(),
                                headers = Headers.build {
                                    append(HttpHeaders.ContentType, "image/jpeg")
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=\"${file.name}\""
                                    )
                                }
                            )
                        }
                    }
                )
                if (response.status.isSuccess()) 200 else 400
            } catch (e: Exception) {
                Log.d("createStreetAnimal", e.toString())
                -1
            }
        }

    }
}