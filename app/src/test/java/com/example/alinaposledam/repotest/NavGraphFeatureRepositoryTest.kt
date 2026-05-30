package com.example.alinaposledam.repotest

import AnnouncementType
import ApiResponse
import SendResult
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import apiService.AnnouncementService
import apiService.AuthService
import apiService.StreetService
import apiService.models.announcement_models.UserPetInfoResponse
import apiService.models.auth_models.UpdateUserInfoRequest
import apiService.models.auth_models.UserInfoResponse
import apiService.models.street_models.StreetAnimalDetailsResponse
import apiService.models.street_models.StreetAnimalRequest
import com.example.alinaposledam.viewmodel.MainDispatcherRule
import data.SearchRepositoryImpl
import data.repository.AnnouncementRepositoryImpl
import data.repository.AuthRepositoryImpl
import data.repository.ImageLoaderRepositoryImpl
import data.repository.MainRepositoryImpl
import data.repository.StreetRepositoryImpl
import db.notification.NotificationDao
import db.notification.NotificationEntity
import db.user.UserDao
import db.user.UserEntity
import domain.UserInfo
import domain.model.AnnouncementInfo
import domain.model.AnnouncementStatus
import domain.model.Location
import domain.model.LoginInfo
import domain.user.model.User
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import model.InternetStatus
import model.announcement.FoundPetResponse
import model.announcement.MissAllDto
import model.announcement.MissAllDtoFound
import model.auth.response.Contact
import model.auth.response.LoginResponse
import org.junit.Rule
import org.junit.Test
import repository.notification.NotificationRepositoryImpl
import repository.notification.NotificationSettingsRepositoryImpl
import repository.user.UserRepositoryImpl
import storage.UserInfoRepository
import ui.model.Response
import ui.model.data.UserDataInfo
import ui.models.FilterDto
import ui.models.TimeFilter
import ui.other.Converter
import ui.viewModel.SpottedAnimalData
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class NavGraphFeatureRepositoryTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `auth repository maps login and register results`() = runTest {
        val service = mockk<AuthService>()
        coEvery { service.login(match { it.email == "mail@test.ru" }) } returns ApiResponse.Success(LoginResponse("token"))
        coEvery { service.register(any()) } returns ApiResponse.Error(403)

        val repository = AuthRepositoryImpl(service)

        assertEquals(true to null, repository.login(LoginInfo("mail@test.ru", "123456")))
        assertEquals(false to 403, repository.register(UserDataInfo(email = "mail@test.ru", password = "123456", name = "Алина")))
    }

    @Test
    fun `main repository caches syncs updates and deletes user`() = runTest {
        val service = mockk<AuthService>()
        val cache = mockk<UserInfoRepository>()
        val cachedUser = UserInfo(id = "old", firstName = "Old")
        val updateRequest = UpdateUserInfoRequest(id = "42", firstName = "Алина", description = "описание")
        coEvery { cache.saveUserInfo(any()) } returns Unit
        coEvery { cache.getUserInfo() } returns cachedUser
        coEvery { cache.deleteUserInfo() } returns Unit
        coEvery { service.updateUserInfo(updateRequest) } returns true
        coEvery { service.getInfo() } returns ApiResponse.Success(userInfoResponse())
        coEvery { service.updateUserImage(any(), "42") } returns Unit

        val repository = MainRepositoryImpl(service, cache)
        repository.saveUserToCached(cachedUser)
        assertEquals(cachedUser, repository.getUserFromCache())
        assertTrue(repository.updateUserInfo(updateRequest, cachedUser))
        val syncResult = repository.syncUserFromServer()
        repository.updateUserImage(File("avatar.jpg"), "42")
        repository.deleteUser()

        assertIs<ApiResponse.Success<UserInfo>>(syncResult)
        assertEquals("Алина", syncResult.data.firstName)
        coVerify(atLeast = 3) { cache.saveUserInfo(any()) }
        coVerify { cache.deleteUserInfo() }
    }

    @Test
    fun `main repository maps sync and image update errors`() = runTest {
        val service = mockk<AuthService>()
        val cache = mockk<UserInfoRepository>()
        coEvery { cache.saveUserInfo(any()) } returns Unit
        coEvery { service.getInfo() } returns ApiResponse.Error(500)
        coEvery { service.updateUserImage(any(), "42") } returns Unit

        val repository = MainRepositoryImpl(service, cache)

        assertEquals(ApiResponse.Error(500), repository.syncUserFromServer())
        repository.updateUserImage(File("avatar.jpg"), "42")

        coVerify(exactly = 0) { cache.saveUserInfo(any()) }
    }

    @Test
    fun `announcement repository maps send and user announcements`() = runTest {
        val service = mockk<AnnouncementService>()
        val converter = mockk<Converter>()
        val file = File.createTempFile("announcement", ".jpg")
        coEvery { converter.convertToFile("photo") } returns file
        coEvery { service.sendAnnouncement(any(), listOf(file), AnnouncementType.Found) } returns SendResult.Success
        coEvery { service.sendAnnouncement(any(), listOf(file), AnnouncementType.Miss) } returns SendResult.Error("network")
        coEvery { service.getUserPets(AnnouncementType.Miss) } returns ApiResponse.Success(
            listOf(UserPetInfoResponse("1", null, "Центральный", null, "2026-05-28T10:00:00Z", "image.jpg"))
        )
        coEvery { service.getUserPets(AnnouncementType.Found) } returns ApiResponse.Error(400)

        val repository = AnnouncementRepositoryImpl(service, converter)
        val info = announcementInfo()

        assertEquals(AnnouncementStatus.Success, repository.sendAnnouncement(info, listOf("photo"), 1))
        assertEquals(AnnouncementStatus.Failed(InternetStatus.NoInternet), repository.sendAnnouncement(info, listOf("photo"), 0))

        val userPets = repository.getUserAnnouncements(0)
        assertEquals("Порода не указана", userPets.first!!.single().breed)
        assertEquals("Нет описания", userPets.first!!.single().description)
        assertEquals(null to 400, repository.getUserAnnouncements(1))
    }

    @Test
    fun `announcement repository maps bad request send result`() = runTest {
        val service = mockk<AnnouncementService>()
        val converter = mockk<Converter>()
        coEvery { service.sendAnnouncement(any(), emptyList(), AnnouncementType.Found) } returns SendResult.BadRequest()

        val repository = AnnouncementRepositoryImpl(service, converter)

        assertEquals(
            AnnouncementStatus.Failed(InternetStatus.Error),
            repository.sendAnnouncement(announcementInfo(), emptyList(), 1)
        )
    }

    @Test
    fun `image loader repository delegates to auth service`() = runTest {
        val service = mockk<AuthService>()
        val file = File.createTempFile("avatar", ".jpg")
        coEvery { service.updateUserImage(file, "42") } returns Unit

        ImageLoaderRepositoryImpl(service).loadImage(file, "42")

        coVerify { service.updateUserImage(file, "42") }
    }

    @Test
    fun `street repository creates advert and maps details`() = runTest {
        val service = mockk<StreetService>()
        val converter = mockk<Converter>()
        val uri = mockk<Uri>()
        val file = File.createTempFile("street", ".jpg")
        every { uri.toString() } returns "street-photo"
        coEvery { converter.convertToFile("street-photo") } returns file
        coEvery { service.createStreetAnimal(any(), listOf(file)) } returns 200
        coEvery { service.getDetailsAboutStreetAnimal("1") } returns ApiResponse.Success(streetDetailsResponse())
        coEvery { service.getDetailsAboutStreetAnimal("bad") } returns ApiResponse.Error(400)

        val repository = StreetRepositoryImpl(service, converter)

        assertEquals(200, repository.createStreetAdvert(domain.models.AdvertInfo(images = listOf(uri), lat = 56.0, lon = 92.0)))
        val details = repository.getInformationAboutStreetAnimal("1")
        assertEquals("рядом с домом", details.first?.placeDescription)
        assertEquals(null to 400, repository.getInformationAboutStreetAnimal("bad"))
        coVerify {
            service.createStreetAnimal(
                match<StreetAnimalRequest> { it.lat == 56.0 && it.lon == 92.0 && it.petType == 2 },
                listOf(file)
            )
        }
    }

    @Test
    fun `search repository maps lists details and report responses`() = runTest {
        val service = mockk<AnnouncementService>()
        val converter = mockk<Converter>()
        val file = File.createTempFile("spot", ".jpg")
        coEvery { converter.convertToFile("spot-uri") } returns file
        coEvery { service.findMissingAnnouncement(any()) } returns ApiResponse.Success(
            listOf(MissAllDto("m1", eventDate = "2026-05-28T10:00:00Z", petName = "Барсик", description = null, mainImagePath = "m.jpg", district = "Центральный"))
        )
        coEvery { service.findFoundAnnouncement(any()) } returns ApiResponse.Success(
            listOf(MissAllDtoFound("f1", eventDate = "2026-05-28T10:00:00Z", description = null, mainImagePath = "f.jpg", district = "Октябрьский", breed = "сибирская"))
        )
        coEvery { service.getInfoAboutPet(any(), AnnouncementType.Found) } returns ApiResponse.Success(foundPetResponse())
        coEvery { service.getInfoAboutPet(any(), AnnouncementType.Miss) } returns ApiResponse.Error(-1)
        coEvery { service.reportFoundAnimal("ok") } returns SendResult.Success
        coEvery { service.reportFoundAnimal("bad") } returns SendResult.BadRequest()
        coEvery { service.reportSpottedAnimal("spot", any(), listOf(file)) } returns SendResult.Error("network")

        val repository = SearchRepositoryImpl(service, converter)

        val missing = repository.findMissingAnnouncement(FilterDto()).first()
        assertEquals("Нет описания", missing.first!!.single().description)
        val found = repository.findFoundAnnouncement(FilterDto()).first()
        assertEquals("сибирская", found.first!!.single().breed)

        val details = repository.getInfoAboutPet("f1", 0)
        assertEquals("Мира", details.first?.street)
        assertNull(details.second)
        assertEquals(null to InternetStatus.NoInternet, repository.getInfoAboutPet("m1", 1))

        assertEquals(Response.SUCCESS, repository.reportFoundAnimal("ok"))
        assertEquals(Response.SERVER_ERROR, repository.reportFoundAnimal("bad"))
        assertEquals(
            Response.INTERNET_ERROR,
            repository.reportSpottedAnimal("spot", SpottedAnimalData(lat = 56.0, lon = 92.0, uri = listOf(mockUri("spot-uri"))))
        )
    }

    @Test
    fun `search repository maps error branches and spotted success bad request`() = runTest {
        val service = mockk<AnnouncementService>()
        val converter = mockk<Converter>()
        val file = File.createTempFile("spot", ".jpg")
        coEvery { converter.convertToFile("spot-uri") } returns file
        coEvery { service.findMissingAnnouncement(any()) } returnsMany listOf(
            ApiResponse.Error(500),
            ApiResponse.Error(400)
        )
        coEvery { service.findFoundAnnouncement(any()) } returnsMany listOf(
            ApiResponse.Error(500),
            ApiResponse.Error(400)
        )
        coEvery { service.getInfoAboutPet(any(), AnnouncementType.Found) } returnsMany listOf(
            ApiResponse.Error(400),
            ApiResponse.Error(418),
            ApiResponse.Success(foundPetResponse().copy(imagesPaths = null))
        )
        coEvery { service.reportFoundAnimal("net") } returns SendResult.Error("network")
        coEvery { service.reportSpottedAnimal("bad", any(), listOf(file)) } returns SendResult.BadRequest()
        coEvery { service.reportSpottedAnimal("ok", any(), emptyList()) } returns SendResult.Success

        val repository = SearchRepositoryImpl(service, converter)

        assertEquals(null to InternetStatus.Error, repository.findMissingAnnouncement(FilterDto()).first())
        assertEquals(null to InternetStatus.Error, repository.findMissingAnnouncement(FilterDto()).first())
        assertEquals(null to InternetStatus.Error, repository.findFoundAnnouncement(FilterDto()).first())
        assertEquals(null to InternetStatus.Error, repository.findFoundAnnouncement(FilterDto()).first())
        assertEquals(null to InternetStatus.Error, repository.getInfoAboutPet("f1", 0))
        assertEquals(null to InternetStatus.Error, repository.getInfoAboutPet("f1", 0))
        assertNull(repository.getInfoAboutPet("f1", 0).first?.imagePath)
        assertEquals(Response.INTERNET_ERROR, repository.reportFoundAnimal("net"))
        assertEquals(
            Response.SERVER_ERROR,
            repository.reportSpottedAnimal("bad", SpottedAnimalData(lat = 56.0, lon = 92.0, uri = listOf(mockUri("spot-uri"))))
        )
        assertEquals(
            Response.SUCCESS,
            repository.reportSpottedAnimal("ok", SpottedAnimalData(lat = 56.0, lon = 92.0))
        )
    }

    @Test
    fun `search repository maps non null filters and preview fallbacks`() = runTest {
        val service = mockk<AnnouncementService>()
        val converter = mockk<Converter>(relaxed = true)
        coEvery { service.findMissingAnnouncement(any()) } returns ApiResponse.Success(
            listOf(
                MissAllDto(
                    id = "m2",
                    createdAt = "2026-05-28T09:00:00Z",
                    eventDate = "2026-05-27T09:00:00Z",
                    petName = "Барсик",
                    description = "живое описание",
                    mainImagePath = "m2.jpg",
                    district = "Центральный"
                )
            )
        )
        coEvery { service.findFoundAnnouncement(any()) } returns ApiResponse.Success(
            listOf(
                MissAllDtoFound(
                    id = "f2",
                    createdAt = "2026-05-28T09:00:00Z",
                    eventDate = "2026-05-27T09:00:00Z",
                    description = "нашли у дома",
                    mainImagePath = "f2.jpg",
                    district = "Центральный",
                    breed = null
                )
            )
        )

        val repository = SearchRepositoryImpl(service, converter)
        val filter = FilterDto(
            district = "Центральный",
            time = TimeFilter.WEEK,
            typeOfPet = 0,
            gender = 1,
            lastDateTime = "2026-05-28T08:00:00Z"
        )

        val missing = repository.findMissingAnnouncement(filter).first().first!!.single()
        val found = repository.findFoundAnnouncement(filter).first().first!!.single()

        assertEquals("живое описание", missing.description)
        assertEquals("2026-05-28T09:00:00Z", missing.createdAt)
        assertEquals("нашли у дома", found.description)
        assertEquals("2026-05-28T09:00:00Z", found.createdAt)
        coVerify {
            service.findMissingAnnouncement(
                match {
                    it.lastDateTime.toString() == "2026-05-28T08:00:00Z" &&
                        it.district == "Центральный" &&
                        it.from != null &&
                        it.type == 0 &&
                        it.gender == 1
                }
            )
        }
    }

    @Test
    fun `user repository maps dao and remote calls`() = runTest {
        val service = mockk<AuthService>()
        val dao = mockk<UserDao>()
        every { dao.observeUser() } returns flowOf(UserEntity("42", "Алина", "описание", "avatar", tg = "tg", wh = "wa", vk = "vk"))
        coEvery { dao.updateUserInfo(any()) } returns Unit
        coEvery { dao.saveUser(any()) } returns Unit
        coEvery { dao.clearUser() } returns Unit
        coEvery { service.updateUserInfo(any()) } returns true
        coEvery { service.getInfo() } returns ApiResponse.Success(userInfoResponse())

        val repository = UserRepositoryImpl(service, dao)
        val observed = repository.observeUser().first()
        repository.updateUser(User(id = "42", name = "Алина", description = "новое", vk = "vk"))
        repository.refreshUser()
        repository.clearUser()

        assertEquals("Алина", observed?.name)
        assertEquals("vk", observed?.vk)
        coVerify { dao.updateUserInfo(match { it.firstName == "Алина" }) }
        coVerify { dao.saveUser(match { it.id == "42" && it.firstName == "Алина" }) }
        coVerify { dao.clearUser() }
    }

    @Test
    fun `user repository ignores refresh error and maps null observed user`() = runTest {
        val service = mockk<AuthService>()
        val dao = mockk<UserDao>()
        every { dao.observeUser() } returns flowOf(null)
        coEvery { service.getInfo() } returns ApiResponse.Error(500)

        val repository = UserRepositoryImpl(service, dao)

        assertNull(repository.observeUser().first())
        repository.refreshUser()

        coVerify(exactly = 0) { dao.saveUser(any()) }
    }

    @Test
    fun `notification repository delegates all operations`() = runTest {
        val dao = mockk<NotificationDao>()
        val entity = NotificationEntity(1L, "title", "body", 10L, false, 0)
        every { dao.getAllNotificationEntity() } returns flowOf(listOf(entity))
        coEvery { dao.insert(entity) } returns Unit
        coEvery { dao.deleteById(1L) } returns Unit
        coEvery { dao.deleteAll() } returns Unit
        coEvery { dao.markIsRead(1L) } returns Unit
        coEvery { dao.allMark() } returns Unit

        val repository = NotificationRepositoryImpl(dao)
        assertEquals(listOf(entity), repository.getAllNotificationEntity().first())
        repository.insert(entity)
        repository.deleteById(1L)
        repository.deleteAll()
        repository.markIsRead(1L)
        repository.allMark()

        coVerify { dao.insert(entity) }
        coVerify { dao.deleteById(1L) }
        coVerify { dao.deleteAll() }
        coVerify { dao.markIsRead(1L) }
        coVerify { dao.allMark() }
    }

    @Test
    fun `notification settings repository reads writes and observes preferences`() = runTest {
        val context = mockk<Context>()
        val prefs = mockk<SharedPreferences>()
        val editor = mockk<SharedPreferences.Editor>()
        every { context.getSharedPreferences("notification_settings", Context.MODE_PRIVATE) } returns prefs
        every { prefs.getBoolean("notifications_enabled", false) } returnsMany listOf(false, true)
        every { prefs.edit() } returns editor
        every { editor.putBoolean("notifications_enabled", true) } returns editor
        every { editor.apply() } returns Unit

        val repository = NotificationSettingsRepositoryImpl(context)
        assertEquals(false, repository.observeNotificationsEnabled().first())
        repository.setNotificationsEnabled(true)
        assertEquals(true, repository.observeNotificationsEnabled().first())
        assertEquals(true, repository.isNotificationsEnabled())

        coVerify { editor.putBoolean("notifications_enabled", true) }
    }

    private fun announcementInfo() = AnnouncementInfo(
        location = Location(56.0, 92.0),
        petType = 0,
        gender = 1,
        color = "рыжий",
        breed = "сибирская",
        petName = "Барсик",
        eventDate = LocalDate.of(2026, 5, 28),
        time = LocalTime.of(12, 30),
        description = "описание"
    )

    private fun userInfoResponse() = UserInfoResponse(
        id = "42",
        contacts = listOf(Contact(0, "vk"), Contact(1, "tg"), Contact(2, "wa")),
        firstName = "Алина",
        avatarPath = "avatar.jpg",
        description = "описание"
    )

    private fun streetDetailsResponse() = StreetAnimalDetailsResponse(
        imagePaths = listOf("image.jpg"),
        creator = StreetAnimalDetailsResponse.CreatorDto("1", "Алина", "avatar.jpg"),
        location = StreetAnimalDetailsResponse.Location(longitude = 92.0, latitude = 56.0),
        eventDate = "2026-05-28T10:00:00Z",
        placeDescription = "рядом с домом"
    )

    private fun foundPetResponse() = FoundPetResponse(
        id = "f1",
        street = "Мира",
        house = "1",
        district = "Центральный",
        imagesPaths = listOf("image.jpg"),
        creator = FoundPetResponse.CreatorDto("1", "Алина", "avatar.jpg"),
        location = FoundPetResponse.Location(longitude = 92.0, latitude = 56.0),
        eventDate = "2026-05-28T10:00:00Z",
        petType = 0,
        gender = 1,
        color = "рыжий",
        breed = "сибирская",
        type = 0,
        description = "описание"
    )

    private fun mockUri(value: String): Uri {
        val uri = mockk<Uri>()
        every { uri.toString() } returns value
        return uri
    }
}
