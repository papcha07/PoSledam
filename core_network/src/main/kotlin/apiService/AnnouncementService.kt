package apiService

import AnnouncementType
import ApiResponse
import SendResult
import android.util.Log
import apiService.models.announcement_models.CancelAnnouncementRequest
import apiService.models.announcement_models.FoundReportResponse
import apiService.models.announcement_models.SpottedLocationResponse
import apiService.models.announcement_models.UserPetInfoResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.utils.io.streams.asInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import model.announcement.AnnouncementRequest
import model.announcement.FoundPetRequest
import model.announcement.FoundPetResponse
import model.announcement.Location
import model.announcement.MissAllDto
import model.announcement.MissAllRequest
import toApiErrorCode
import toSendResultError
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

                else -> SendResult.BadRequest("HTTP ${response.status.value}")
            }
        } catch (e: Exception) {
            e.toSendResultError(
                networkMessage = e.message ?: "Unknown error"
            )
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
                                    appendFilePart(key = "Images", file = file)
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
                return@withContext e.toSendResultError(
                    networkMessage = "Проблемы с соединением"
                )
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
            return ApiResponse.Error(e.toApiErrorCode())
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
            ApiResponse.Error(e.toApiErrorCode())
        }
    }

    suspend fun getSpottedLocations(
        announcementId: String
    ): ApiResponse<List<SpottedLocationResponse>> {
        return try {
            val response = client.get("api/$MISS/$announcementId/spotted-locations")
            when {
                response.status.isSuccess() -> {
                    ApiResponse.Success(response.body<List<SpottedLocationResponse>>())
                }

                else -> ApiResponse.Error(response.status.value)
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.toApiErrorCode())
        }
    }

    suspend fun getFoundReports(
        announcementId: String
    ): ApiResponse<List<FoundReportResponse>> {
        return try {
            val response = client.get("api/$MISS/$announcementId/found-reports")
            when {
                response.status.isSuccess() -> {
                    ApiResponse.Success(response.body<List<FoundReportResponse>>())
                }

                else -> ApiResponse.Error(response.status.value)
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.toApiErrorCode())
        }
    }

    suspend fun findMissingAnnouncement(missAllInfo: MissAllRequest): ApiResponse<List<MissAllDto>> {
        return try {
            Log.d("MissAllRequest", missAllInfo.toString())
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
                ApiResponse.Success(response.body<List<MissAllDto>>())
            } else {
                ApiResponse.Error(response.status.value)
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.toApiErrorCode())
        }
    }

    suspend fun findFoundAnnouncement(missAllInfo: MissAllRequest): ApiResponse<List<MissAllDto>> {
        return try {
            Log.d("MissAllRequest", missAllInfo.toString())

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
                ApiResponse.Success(response.body<List<MissAllDto>>())
            } else {
                ApiResponse.Error(response.status.value)
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.toApiErrorCode())
        }
    }

    suspend fun cancelMissAnnouncement(
        cancelAnnouncementRequest: CancelAnnouncementRequest,
        type: AnnouncementType
    ): SendResult {
        return withContext(Dispatchers.IO) {
            try {
                val urlType = if (type == AnnouncementType.Miss) MISS else FIND
                val request = client.post("api/$urlType/cancel/${cancelAnnouncementRequest.id}") {
                    setBody(
                        if (type == AnnouncementType.Miss) {
                            CancelMissingAnnouncementBody(
                                deleteReason = cancelAnnouncementRequest.deleteReason
                            )
                        } else {
                            CancelFindAnnouncementBody(
                                cancelReason = cancelAnnouncementRequest.deleteReason
                            )
                        }
                    )
                }
                if (request.status.isSuccess()) {
                    SendResult.Success
                } else {
                    SendResult.BadRequest()
                }
            } catch (e: Exception) {
                e.toSendResultError(
                    networkMessage = "Проблемы с соединением"
                )
            }

        }
    }


    suspend fun reportFoundAnimal(id: String, files: List<File>): SendResult {
        return withContext(Dispatchers.IO) {
            try {
                val response = client.post("api/$MISS/$id/report-found") {
                    setBody(
                        MultiPartFormDataContent(
                            formData {
                                files.forEach { file ->
                                    appendFilePart(key = "Images", file = file)
                                }
                            }
                        )
                    )
                }
                if (response.status.isSuccess()) {
                    SendResult.Success
                } else {
                    SendResult.BadRequest()
                }
            } catch (e: Exception) {
                e.toSendResultError(
                    networkMessage = e.message ?: "Unknown error"
                )
            }
        }
    }

    suspend fun reportAnnouncement(
        announcementId: String,
        comment: String
    ): SendResult = withContext(Dispatchers.IO) {
        try {
            val response = client.post("api/animal-announcement/$announcementId/report") {
                contentType(ContentType.Application.Json)
                setBody(ReportAnnouncementBody(comment = comment))
            }
            when {
                response.status.isSuccess() -> SendResult.Success
                response.status.value == 400 ||
                        response.status.value == 403 ||
                        response.status.value == 404 ||
                        response.status.value == 409 -> {
                    val text = runCatching { response.bodyAsText() }.getOrNull()
                    SendResult.BadRequest(message = text ?: "Bad request")
                }

                else -> SendResult.BadRequest("HTTP ${response.status.value}")
            }
        } catch (e: Exception) {
            e.toSendResultError(
                networkMessage = e.message ?: "Unknown error"
            )
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
                    appendFilePart(key = "Images", file = file)
                }
            }
        )
    }

    companion object {
        private const val MISS = "missing-announcement"
        private const val FIND = "find-announcement"
    }
}

@Serializable
private data class CancelMissingAnnouncementBody(
    val deleteReason: Int
)

@Serializable
private data class CancelFindAnnouncementBody(
    val cancelReason: Int
)

@Serializable
private data class ReportAnnouncementBody(
    val comment: String
)

internal fun FormBuilder.appendFilePart(
    key: String,
    file: File,
    contentType: ContentType = ContentType.Image.JPEG
) {
    appendInput(
        key = key,
        headers = Headers.build {
            append(HttpHeaders.ContentType, contentType.toString())
            append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
        },
        size = file.length()
    ) {
        file.inputStream().asInput()
    }
}
