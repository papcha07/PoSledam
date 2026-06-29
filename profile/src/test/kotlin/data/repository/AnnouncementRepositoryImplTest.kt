package data.repository

import AnnouncementType
import ApiResponse
import apiService.AnnouncementService
import apiService.models.announcement_models.FoundReportContactResponse
import apiService.models.announcement_models.FoundReportResponse
import apiService.models.announcement_models.FoundReportUserResponse
import apiService.models.announcement_models.SpottedLocationDto
import apiService.models.announcement_models.SpottedLocationResponse
import apiService.models.announcement_models.SpottedUserResponse
import apiService.models.announcement_models.UserPetInfoResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import model.InternetStatus
import model.announcement.FoundPetRequest
import model.announcement.FoundPetResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import ui.other.Converter

class AnnouncementRepositoryImplTest {

    private val apiService: AnnouncementService = mockk()
    private val converter: Converter = mockk()
    private val repository = AnnouncementRepositoryImpl(apiService, converter)

    @Test
    fun `getUserAnnouncements maps responses and fallback text`() = runTest {
        coEvery { apiService.getUserPets(AnnouncementType.Miss) } returns ApiResponse.Success(
            listOf(
                UserPetInfoResponse(
                    id = "pet-id",
                    breed = null,
                    district = "Центральный",
                    description = null,
                    createdAt = "2026-06-23T10:00:00Z",
                    mainImagePath = "image.jpg"
                )
            )
        )

        val result = repository.getUserAnnouncements(type = 0)

        assertNull(result.second)
        assertEquals(1, result.first?.size)
        assertEquals("pet-id", result.first?.first()?.id)
        assertEquals("Порода не указана", result.first?.first()?.breed)
        assertEquals("Нет описания", result.first?.first()?.description)
        assertEquals("Центральный", result.first?.first()?.district)
        assertEquals("image.jpg", result.first?.first()?.imageUrl)
    }

    @Test
    fun `getAnnouncementDetails maps details coordinates image and date`() = runTest {
        coEvery {
            apiService.getInfoAboutPet(FoundPetRequest("announcement-id"), AnnouncementType.Found)
        } returns ApiResponse.Success(
            FoundPetResponse(
                id = "announcement-id",
                street = "Ленина",
                house = "10",
                district = "Центральный",
                imagesPaths = listOf("first.jpg", "second.jpg"),
                creator = FoundPetResponse.CreatorDto(id = "user-id", firstName = "Анна"),
                location = FoundPetResponse.Location(longitude = 92.85, latitude = 56.01),
                eventDate = "2026-06-23T12:30:00Z",
                petType = 1,
                gender = 0,
                color = "рыжий",
                breed = "корги",
                type = 1,
                description = "нашли возле парка"
            )
        )

        val result = repository.getAnnouncementDetails(id = "announcement-id", type = 1)
        val details = requireNotNull(result.first)

        assertNull(result.second)
        assertEquals("announcement-id", details.id)
        assertEquals("first.jpg", details.imagePath)
        assertEquals(56.01, details.latitude, 0.0)
        assertEquals(92.85, details.longitude, 0.0)
        assertEquals("23.06.2026", details.eventDate)
        assertEquals("Ленина", details.street)
        assertEquals("10", details.house)
    }

    @Test
    fun `getSpottedLocations maps user name point images and no internet error`() = runTest {
        coEvery { apiService.getSpottedLocations("announcement-id") } returns ApiResponse.Success(
            listOf(
                SpottedLocationResponse(
                    id = "spot-id",
                    spottedUser = SpottedUserResponse(
                        id = "user-id",
                        firstName = "Иван",
                        secondName = "Петров"
                    ),
                    createdAt = "2026-06-23T12:30:00Z",
                    location = SpottedLocationDto(latitude = 56.02, longitude = 92.86),
                    imagesPath = listOf("spot.jpg")
                )
            )
        )

        val success = repository.getSpottedLocations("announcement-id")

        assertNull(success.second)
        assertEquals("spot-id", success.first?.first()?.id)
        assertEquals("Иван Петров", success.first?.first()?.spottedUserName)
        assertEquals(56.02, success.first?.first()?.latitude ?: 0.0, 0.0)
        assertEquals(92.86, success.first?.first()?.longitude ?: 0.0, 0.0)
        assertEquals(listOf("spot.jpg"), success.first?.first()?.imagesPath)

        coEvery { apiService.getSpottedLocations("announcement-id") } returns ApiResponse.Error(-1)

        val error = repository.getSpottedLocations("announcement-id")

        assertNull(error.first)
        assertEquals(InternetStatus.NoInternet, error.second)
    }

    @Test
    fun `getFoundReports filters blank contacts and keeps images`() = runTest {
        coEvery { apiService.getFoundReports("announcement-id") } returns ApiResponse.Success(
            listOf(
                FoundReportResponse(
                    id = "report-id",
                    spottedUser = FoundReportUserResponse(
                        id = "user-id",
                        firstName = "Мария",
                        secondName = null,
                        contacts = listOf(
                            FoundReportContactResponse(contactType = 1, url = "@maria"),
                            FoundReportContactResponse(contactType = 2, url = "")
                        )
                    ),
                    createdAt = "2026-06-23T12:30:00Z",
                    imagesPath = listOf("report.jpg")
                )
            )
        )

        val result = repository.getFoundReports("announcement-id")
        val report = requireNotNull(result.first).first()

        assertNull(result.second)
        assertEquals("report-id", report.id)
        assertEquals("Мария", report.user.name)
        assertEquals(listOf("report.jpg"), report.imagesPath)
        assertEquals(1, report.user.contacts.size)
        assertEquals(1, report.user.contacts.first().type)
        assertEquals("@maria", report.user.contacts.first().url)
        coVerify { apiService.getFoundReports("announcement-id") }
    }
}
