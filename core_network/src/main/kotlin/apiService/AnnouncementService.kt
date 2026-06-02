package apiService

import AnnouncementType
import ApiResponse
import SendResult
import android.util.Log
import apiService.models.announcement_models.UserPetInfoResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.announcement.AnnouncementRequest
import model.announcement.FoundPetRequest
import model.announcement.FoundPetResponse
import model.announcement.Location
import model.announcement.MissAllDto
import model.announcement.MissAllRequest
import java.io.File


class AnnouncementService(private val client: HttpClient) {

    suspend fun sendAnnouncement(
        announcementRequest: AnnouncementRequest,
        files: List<File>,
        type: AnnouncementType
    ): SendResult = withContext(Dispatchers.IO) {
        try {
            val type =
                if (type == AnnouncementType.Miss) MISS else FIND
            val response = client.post("api/$type") {
                setBody(buildFindAnnouncementForm(announcementRequest, files))
                Log.d("sendAnnouncementService", announcementRequest.toString())
            }
            when {
                response.status.isSuccess() -> SendResult.Success
                response.status.value == 400 -> {
                    val text = runCatching { response.bodyAsText() }.getOrNull()
                    SendResult.BadRequest(message = text ?: "Bad request")
                }

                else -> SendResult.Error("HTTP ${response.status.value}")
            }
        } catch (e: Exception) {
            SendResult.Error(e.message ?: "Unknown error")
        }
    }


    suspend fun reportSpottedAnimal(
        id: String,
        reportRequest: Location,
        files: List<File>
    ): SendResult {
        return withContext(Dispatchers.IO) {
            try {
                val response = client.post("api/$MISS/$id/report-spotted") {
                    setBody(
                        MultiPartFormDataContent(
                            formData {
                                append("Coordinates.Latitude", reportRequest.latitude)
                                append("Coordinates.Longitude", reportRequest.longitude)
                                files.forEach { file ->
                                    append(
                                        key = "Images",
                                        value = file.readBytes(),
                                        headers = Headers.build {
                                            append(HttpHeaders.ContentType, "image/jpeg")
                                            append(
                                                HttpHeaders.ContentDisposition,
                                                "form-data; name=\"Images\"; filename=\"${file.name}\""
                                            )
                                        }
                                    )
                                }
                            }
                        )
                    )
                }
                when {
                    response.status.isSuccess() -> return@withContext SendResult.Success
                    else -> return@withContext SendResult.BadRequest()
                }

            } catch (e: Exception) {
                return@withContext SendResult.Error("Проблемы с соединением")
            }
        }
    }

    suspend fun getUserPets(type: AnnouncementType): ApiResponse<List<UserPetInfoResponse>> {
        try {
            val announcementType =
                if (type == AnnouncementType.Miss) MISS else FIND
            val response = client.get("api/$announcementType/me/feed")
            when {
                response.status.isSuccess() -> {
                    val body = response.body<List<UserPetInfoResponse>>()
                    Log.d("pets", body.toString())
                    return ApiResponse.Success(body)
                }

                else -> return ApiResponse.Error(400)
            }
        } catch (e: Exception) {
            return ApiResponse.Error(-1)
        }
    }

    suspend fun getInfoAboutPet(
        foundRequest: FoundPetRequest,
        announcementType: AnnouncementType
    ): ApiResponse<FoundPetResponse> {
        return try {
            val announcement =
                if (announcementType == AnnouncementType.Miss) MISS else FIND
            val response = client.get("api/$announcement/${foundRequest.id}")
            when {
                response.status.isSuccess() -> {
                    val body = response.body<FoundPetResponse>()
                    Log.d("FoundPetResponse", body.toString())
                    ApiResponse.Success(body)
                }

                else -> ApiResponse.Error(400)
            }
        } catch (e: Exception) {
            ApiResponse.Error(-1)
        }
    }

    suspend fun findMissingAnnouncement(missAllInfo: MissAllRequest): ApiResponse<List<MissAllDto>> {
        val response = client.get("api/missing-announcement/feed") {
            url {
                missAllInfo.lastDateTime?.let {
                    parameters.append("lastDateTime", it.toString())
                }
                missAllInfo.district?.let {
                    parameters.append("district", it)
                }
                missAllInfo.type?.let {
                    parameters.append("type", it.toString())
                }
                missAllInfo.gender?.let {
                    parameters.append("gender", it.toString())
                }
                missAllInfo.searchRadius?.let {
                    parameters.append("SearchRadius", it.toString())
                }
                missAllInfo.searchCenterLatitude?.let {
                    parameters.append("SearchCenter.Latitude", it.toString())
                }
                missAllInfo.searchCenterLongitude?.let {
                    parameters.append("SearchCenter.Longitude", it.toString())
                }

            }
        }
        if (response.status.isSuccess()) {
            return ApiResponse.Success(response.body<List<MissAllDto>>())
        } else {
            return ApiResponse.Error(response.status.value)
        }
    }

    suspend fun findFoundAnnouncement(missAllInfo: MissAllRequest): ApiResponse<List<MissAllDto>> {
        val response = client.get("api/find-announcement/feed") {
            url {
                missAllInfo.lastDateTime?.let {
                    parameters.append("lastDateTime", it.toString())
                }
                missAllInfo.district?.let {
                    parameters.append("district", it)
                }
                missAllInfo.type?.let {
                    parameters.append("type", it.toString())
                }
                missAllInfo.gender?.let {
                    parameters.append("gender", it.toString())
                }
                missAllInfo.searchRadius?.let {
                    parameters.append("SearchRadius", it.toString())
                }
                missAllInfo.searchCenterLatitude?.let {
                    parameters.append("SearchCenter.Latitude", it.toString())
                }
                missAllInfo.searchCenterLongitude?.let {
                    parameters.append("SearchCenter.Longitude", it.toString())
                }
            }
        }
        if (response.status.isSuccess()) {
            return ApiResponse.Success(response.body<List<MissAllDto>>())
        } else {
            return ApiResponse.Error(response.status.value)
        }
    }

    suspend fun reportFoundAnimal(id: String): SendResult {
        return withContext(Dispatchers.IO) {
            try {
                val response = client.post("api/$MISS/$id/report-found")
                if (response.status.isSuccess()) {
                    SendResult.Success
                } else SendResult.BadRequest()
            } catch (e: Exception) {
                SendResult.Error("Проблемы с соединением")
            }
        }
    }

    private fun buildFindAnnouncementForm(
        req: AnnouncementRequest,
        files: List<File>
    ): MultiPartFormDataContent {
        return MultiPartFormDataContent(
            formData {
                append("Location.Latitude", req.location.latitude)
                append("Location.Longitude", req.location.longitude)
                append("PetType", req.petType)
                append("Gender", req.gender)
                append("PetName", req.petName)
                append("eventDate", req.eventDate)
                req.color?.takeIf { !it.isBlank() }?.let {
                    append("Color", req.color)
                }
                req.breed?.takeIf { !it.isBlank() }?.let {
                    append("Breed", req.breed)
                }
                req.description?.takeIf { !it.isBlank() }?.let {
                    append("Description", req.description)
                }

                files.forEach { file ->
                    append(
                        key = "Images",
                        value = file.readBytes(),
                        headers = Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(
                                HttpHeaders.ContentDisposition,
                                "form-data; name=\"Images\"; filename=\"${file.name}\""
                            )
                        }
                    )
                }
            }
        )
    }

    companion object {
        private const val MISS = "missing-announcement"
        private const val FIND = "find-announcement"
    }
}