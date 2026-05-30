package com.example.alinaposledam.viewmodel

import android.location.Location
import android.net.Uri
import androidx.paging.PagingData
import app.cash.turbine.test
import domain.LocationProvider
import domain.interactor.SearchInteractor
import domain.interactor.announcement.AnnouncementInteractor
import domain.interactor.loader.ImageLoaderInteractor
import domain.interactor.street.StreetPetInteractor
import domain.model.AnnouncementStatus
import domain.models.Creator
import domain.models.CreatorDetails
import domain.models.DateInfo
import domain.models.FoundPetInfo
import domain.models.PetInfo
import domain.models.PetUiPreview
import domain.models.StreetDetails
import domain.notification.Notification
import domain.notification.NotificationInteractor
import domain.notification.NotificationSettingsInteractor
import domain.user.UserInteractor
import domain.user.model.User
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import model.InternetStatus
import model.geo.AddressSuggestion
import org.junit.Rule
import org.junit.Test
import ui.components.profilebar.ProfileBarState
import ui.model.Response
import ui.model.ActionScreenState
import ui.model.data.UserDataInfo
import ui.model.state.AuthScreenState
import ui.models.SearchState
import ui.models.TimeFilter
import ui.screen.camera.CameraViewModel
import ui.screen.mainScreen.MainScreenViewModel
import ui.screen.street.StreetPetViewModel
import ui.viewModel.ActionPage
import ui.viewModel.ActionViewModel
import ui.viewModel.FilterViewModel
import ui.viewModel.PetDetailsScreenState
import ui.viewModel.ProfileSettingsViewModel
import ui.viewModel.ProfileViewModel
import ui.viewModel.ReportFoundAnimalEffect
import ui.viewModel.ReportViewModel
import ui.login.LoginViewModel
import ui.register.RegisterViewModel
import usecases.AuthInteractor
import yandex_core.NetworkResource
import yandex_core.YandexInteractor
import java.time.LocalDate
import java.time.LocalTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class NavGraphFeatureViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `login emits loading and success`() = runTest {
        val interactor = mockk<AuthInteractor>()
        coEvery { interactor.login(any()) } returns (true to null)

        val viewModel = LoginViewModel(interactor)

        viewModel.loginUiState.test {
            viewModel.login(domain.model.LoginInfo("mail@test.ru", "123456"))
            assertEquals(AuthScreenState.Loading, awaitItem())
            assertEquals(AuthScreenState.Success, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `register stores form data contacts and handles duplicate account`() = runTest {
        val interactor = mockk<AuthInteractor>()
        coEvery { interactor.register(any()) } returns (false to 403)
        val viewModel = RegisterViewModel(interactor)

        viewModel.setEmail("mail@test.ru")
        viewModel.setPassword("123456")
        viewModel.setName("Алина")
        viewModel.setDescription("Описание")
        viewModel.addVk("vk")
        viewModel.addVk("vk-new")
        viewModel.addTelegram("tg")
        viewModel.onNextClicked()
        viewModel.onNextClicked()
        viewModel.onNextClicked()
        viewModel.onBackClicker()

        assertEquals(1, viewModel.currentPage.value)
        with(viewModel.userDataInfoState.value) {
            assertEquals("mail@test.ru", email)
            assertEquals("123456", password)
            assertEquals("Алина", name)
            assertEquals("Описание", description)
            assertEquals(listOf(0, 1), contacts.map { it.contactType })
            assertEquals("vk-new", contacts.first { it.contactType == 0 }.url)
        }

        viewModel.registerUiState.test {
            viewModel.registerUser()
            assertEquals(AuthScreenState.Loading, awaitItem())
            assertEquals(AuthScreenState.Error("Такой аккаунт уже есть"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `register handles success generic error page bounds and whatsapp`() = runTest {
        val interactor = mockk<AuthInteractor>()
        coEvery { interactor.register(any()) } returnsMany listOf(true to null, false to 500)
        val viewModel = RegisterViewModel(interactor)

        viewModel.onBackClicker()
        assertEquals(0, viewModel.currentPage.value)
        repeat(5) { viewModel.onNextClicked() }
        assertEquals(2, viewModel.currentPage.value)
        viewModel.resetPage()
        assertEquals(0, viewModel.currentPage.value)

        viewModel.addWhatsApp("wa")
        assertEquals(2, viewModel.userDataInfoState.value.contacts.single().contactType)

        viewModel.registerUiState.test {
            viewModel.registerUser()
            assertEquals(AuthScreenState.Loading, awaitItem())
            assertEquals(AuthScreenState.Success, awaitItem())
            viewModel.registerUser()
            assertEquals(AuthScreenState.Loading, awaitItem())
            assertEquals(AuthScreenState.Error("Что-то пошло не так"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `register private contact helper returns contact or empty value`() {
        val viewModel = RegisterViewModel(mockk(relaxed = true))
        val method = RegisterViewModel::class.java.getDeclaredMethod(
            "getContact",
            UserDataInfo::class.java,
            Int::class.javaPrimitiveType
        ).apply { isAccessible = true }
        val userData = UserDataInfo(
            contacts = listOf(
                UserDataInfo.ContactType(null, "empty-type"),
                UserDataInfo.ContactType(0, "vk")
            )
        )

        assertEquals("vk", method.invoke(viewModel, userData, 0))
        assertEquals("", method.invoke(viewModel, userData, 1))
    }

    @Test
    fun `main screen observes notifications user and actions`() = runTest {
        val notifications = MutableStateFlow(
            listOf(Notification(1L, "title", "body", false, 0, 10L))
        )
        val notificationInteractor = mockk<NotificationInteractor>()
        val userInteractor = mockk<UserInteractor>()
        every { notificationInteractor.getAllNotificationEntity() } returns notifications
        every { userInteractor.observeUser() } returns flowOf(User(id = "u1", name = "Алина"))
        coEvery { notificationInteractor.deleteById(any()) } returns Unit
        coEvery { userInteractor.refreshUser() } returns Unit

        val viewModel = MainScreenViewModel(notificationInteractor, userInteractor)
        viewModel.markNotificationState.test {
            viewModel.observeUser()
            viewModel.refreshUser()
            viewModel.deleteById(1L)
            assertTrue(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertIs<ProfileBarState.Success>(viewModel.userInfoState.value)
        coVerify { userInteractor.refreshUser() }
        coVerify { notificationInteractor.deleteById(1L) }
    }

    @Test
    fun `street pet details expose success and errors`() = runTest {
        val interactor = mockk<StreetPetInteractor>()
        every { interactor.getStreetAnimals() } returns flowOf(PagingData.empty())
        coEvery { interactor.getInfoAboutStreetAnimal("ok") } returns (streetDetails() to null)
        coEvery { interactor.getInfoAboutStreetAnimal("bad") } returns (null to 400)
        coEvery { interactor.getInfoAboutStreetAnimal("net") } returns (null to -1)
        val viewModel = StreetPetViewModel(interactor)

        viewModel.getDetailsAboutAnimal("ok")
        advanceUntilIdle()
        assertIs<ui.model.ScreenState.Success<StreetDetails>>(viewModel.detailsState.value)

        viewModel.getDetailsAboutAnimal("bad")
        advanceUntilIdle()
        assertEquals(ui.model.ScreenState.Error, viewModel.detailsState.value)

        viewModel.getDetailsAboutAnimal("net")
        advanceUntilIdle()
        assertEquals(ui.model.ScreenState.InternetError, viewModel.detailsState.value)
    }

    @Test
    fun `camera view model edits advert loads address and publishes create result`() = runTest {
        val yandexInteractor = mockk<YandexInteractor>()
        val locationProvider = mockk<LocationProvider>()
        val streetInteractor = mockk<StreetPetInteractor>()
        val uri = mockk<Uri>()
        val location = mockk<Location>()
        every { location.latitude } returns 56.0
        every { location.longitude } returns 92.0
        coEvery { locationProvider.getCurrentLocation() } returns location
        coEvery { yandexInteractor.resolvePointOnceOne(92.0, 56.0) } returns
            NetworkResource.Success(AddressSuggestion("Красноярск", null, null, "Красноярск", 92.0, 56.0))
        coEvery { streetInteractor.createStreetAdvert(any()) } returns 200
        every { streetInteractor.getStreetAnimals() } returns emptyFlow()

        val viewModel = CameraViewModel(yandexInteractor, locationProvider, streetInteractor)
        viewModel.addPhoto(uri)
        viewModel.addDescription("у подъезда")
        viewModel.loadMyLocation()
        advanceUntilIdle()
        viewModel.createStreetAdvert()
        advanceUntilIdle()

        assertEquals(listOf(uri), viewModel.uris.value)
        assertEquals("у подъезда", viewModel.advertState.value.placeDescription)
        assertEquals("Красноярск", viewModel.advertState.value.address)
        assertTrue(viewModel.advertState.value.isPlaced)

        viewModel.removePhoto(uri)
        viewModel.clearViewModel()
        assertTrue(viewModel.uris.value.isEmpty())
        assertFalse(viewModel.advertState.value.isPlaced)
    }

    @Test
    fun `profile settings observes updates notifications image and logout`() = runTest {
        val userInteractor = mockk<UserInteractor>()
        val notificationSettingsInteractor = mockk<NotificationSettingsInteractor>()
        val imageLoaderInteractor = mockk<ImageLoaderInteractor>()
        every { userInteractor.observeUser() } returns flowOf(User(id = "42", name = "Алина", avatarPath = "avatar.jpg"))
        coEvery { notificationSettingsInteractor.isNotificationsEnabled() } returns false
        coEvery { notificationSettingsInteractor.setNotificationsEnabled(any()) } returns Unit
        coEvery { userInteractor.updateUserInfo(any()) } returns Unit
        coEvery { userInteractor.clearUser() } returns Unit
        coEvery { imageLoaderInteractor.loadImage(any(), any()) } returns Unit

        val viewModel = ProfileSettingsViewModel(userInteractor, notificationSettingsInteractor, imageLoaderInteractor)
        viewModel.observeUser()
        advanceUntilIdle()
        viewModel.setName("Новое имя")
        viewModel.setDescription("Новое описание")
        viewModel.addVk("vk")
        viewModel.addTelegram("tg")
        viewModel.addWhatsApp("wa")
        viewModel.setNotificationsEnabled(true)
        viewModel.updateUserInfo()
        viewModel.updateImage()
        viewModel.logout()
        advanceUntilIdle()

        assertTrue(viewModel.notificationsEnabled.value)
        assertEquals("vk", viewModel.profileInfoState.value.vk)
        assertIs<ProfileBarState.Success>(viewModel.userInfoState.value)
        coVerify { userInteractor.updateUserInfo(any()) }
        coVerify { imageLoaderInteractor.loadImage("avatar.jpg", "42") }
        coVerify { userInteractor.clearUser() }
    }

    @Test
    fun `action view model validates form and sends announcement`() = runTest {
        val announcementInteractor = mockk<AnnouncementInteractor>()
        val yandexInteractor = mockk<YandexInteractor>()
        val notificationSettingsInteractor = mockk<NotificationSettingsInteractor>()
        coEvery { notificationSettingsInteractor.isNotificationsEnabled() } returns false
        coEvery { notificationSettingsInteractor.setNotificationsEnabled(true) } returns Unit
        coEvery { announcementInteractor.sendAnnouncement(any(), any(), any()) } returns AnnouncementStatus.Success
        coEvery { yandexInteractor.resolvePointOnceOne(92.0, 56.0) } returns
            NetworkResource.Success(AddressSuggestion("Красноярск", null, null, "ул. Мира", 92.0, 56.0))

        val viewModel = ActionViewModel(announcementInteractor, yandexInteractor, notificationSettingsInteractor)
        viewModel.updateName("Барсик")
        viewModel.updateBreed("Сибирская")
        viewModel.updateColor("рыжий")
        viewModel.updateDescription("потерялся")
        viewModel.updateTypeOfPet(0)
        viewModel.updateGender(1)
        viewModel.updateLatitude(56.0)
        viewModel.updateLongitude(92.0)
        viewModel.updateSelectedDate(LocalDate.of(2026, 5, 28))
        viewModel.updateSelectedTime(LocalTime.of(12, 30))
        viewModel.updateMethodValue(1)
        viewModel.goToAddressPage()
        viewModel.getAddressList(92.0, 56.0)
        advanceUntilIdle()
        viewModel.enableNotifications()
        viewModel.createAnnouncement()
        advanceUntilIdle()

        assertEquals(ActionPage.ADDRESS, viewModel.pageState.value)
        assertEquals("ул. Мира", viewModel.addressText)
        assertTrue(viewModel.notificationsEnabled.value)
        assertEquals(ActionScreenState.SuccessAction, viewModel.uiState.value)
        coVerify { announcementInteractor.sendAnnouncement(any(), emptyList(), 1) }
    }

    @Test
    fun `action view model covers navigation failed send images and address reset`() = runTest {
        val announcementInteractor = mockk<AnnouncementInteractor>()
        val yandexInteractor = mockk<YandexInteractor>(relaxed = true)
        val notificationSettingsInteractor = mockk<NotificationSettingsInteractor>()
        val uri = mockk<Uri>()
        coEvery { notificationSettingsInteractor.isNotificationsEnabled() } returns true
        coEvery { announcementInteractor.sendAnnouncement(any(), any(), any()) } returns AnnouncementStatus.Failed(InternetStatus.Error)

        val viewModel = ActionViewModel(announcementInteractor, yandexInteractor, notificationSettingsInteractor)
        viewModel.updateScreenState()
        assertEquals(ActionPage.ADDRESS, viewModel.pageState.value)
        viewModel.updateScreenState()
        assertEquals(ActionPage.MAIN, viewModel.pageState.value)
        viewModel.goToResultPage()
        assertEquals(ActionPage.RESULT, viewModel.pageState.value)
        viewModel.goToMainPage()
        assertEquals(ActionPage.MAIN, viewModel.pageState.value)

        viewModel.addImage(uri)
        viewModel.removeImage(uri)
        assertTrue(viewModel.state.value.selectedImageUris.isEmpty())
        viewModel.clearAddressRow()
        assertEquals("", viewModel.addressText)

        viewModel.updateName("Барсик")
        viewModel.updateBreed("Сибирская")
        viewModel.updateColor("рыжий")
        viewModel.updateDescription("потерялся")
        viewModel.updateTypeOfPet(0)
        viewModel.updateGender(1)
        viewModel.updateLatitude(56.0)
        viewModel.updateLongitude(92.0)
        viewModel.updateSelectedDate(LocalDate.of(2026, 5, 28))
        viewModel.updateSelectedTime(LocalTime.of(12, 30))
        viewModel.createAnnouncement()
        advanceUntilIdle()

        assertEquals(ActionScreenState.FailedAction("Что-то пошло не так"), viewModel.uiState.value)
        viewModel.dismissSuccess()
        assertEquals(ActionScreenState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `filter view model manages chips lists pagination and details`() = runTest {
        val interactor = mockk<SearchInteractor>()
        val firstPage = List(20) { petPreview(id = "found-$it", createdAt = "2026-05-28T10:00:${it.toString().padStart(2, '0')}Z") }
        val nextPage = listOf(petPreview(id = "found-next", createdAt = "2026-05-28T10:01:00Z"))
        coEvery { interactor.findFoundAnnouncement(match { it.lastDateTime == null }) } returns flowOf(firstPage to null)
        coEvery { interactor.findFoundAnnouncement(match { it.lastDateTime != null }) } returns flowOf(nextPage to null)
        coEvery { interactor.findMissingAnnouncement(any()) } returns flowOf(emptyList<PetUiPreview>() to null)
        coEvery { interactor.getInfoAboutPet("pet-1", 1) } returns (foundPetInfo() to null)
        coEvery { interactor.getInfoAboutPet("pet-2", 1) } returns (null to InternetStatus.NoInternet)

        val viewModel = FilterViewModel(interactor)
        viewModel.setDistrict("Центральный")
        viewModel.setTime(TimeFilter.TODAY)
        viewModel.setType(0)
        viewModel.setGender(1)
        advanceUntilIdle()
        assertEquals(listOf("district", "time", "type", "gender"), viewModel.chips.value.map { it.key })

        viewModel.clearChip("district")
        viewModel.clearChip("time")
        viewModel.clearChip("type")
        viewModel.clearChip("gender")
        viewModel.setTime(TimeFilter.TODAY)
        viewModel.setType(0)
        viewModel.setGender(1)
        viewModel.findFoundPets()
        advanceUntilIdle()
        assertEquals(SearchState.Success, viewModel.foundState.value)
        assertEquals(20, viewModel.foundResults.value.size)
        assertTrue(viewModel.hasMoreFound.value)

        viewModel.loadMoreFoundPets()
        advanceUntilIdle()
        assertEquals(21, viewModel.foundResults.value.size)
        assertFalse(viewModel.hasMoreFound.value)

        viewModel.findMissingPets()
        advanceUntilIdle()
        assertEquals(SearchState.Success, viewModel.missingState.value)
        assertFalse(viewModel.hasMoreMissing.value)

        viewModel.setCurrentTab(1)
        viewModel.getInfoAboutPet("pet-1")
        advanceUntilIdle()
        assertIs<PetDetailsScreenState.Success>(viewModel.petInfoState.value)
        viewModel.getInfoAboutPet("pet-2")
        advanceUntilIdle()
        assertEquals(PetDetailsScreenState.Failed("Проблемы с интернетом"), viewModel.petInfoState.value)
    }

    @Test
    fun `filter view model covers chip variants errors and load more guards`() = runTest {
        val interactor = mockk<SearchInteractor>()
        val firstMissing = List(20) { petPreview(id = "missing-$it", createdAt = "2026-05-28T11:00:${it.toString().padStart(2, '0')}Z") }
        val nextMissing = listOf(petPreview(id = "missing-next", createdAt = "2026-05-28T11:01:00Z"))
        coEvery { interactor.findFoundAnnouncement(any()) } returns flow { throw IllegalStateException("boom") }
        coEvery { interactor.findMissingAnnouncement(match { it.lastDateTime == null }) } returns flowOf(firstMissing to null)
        coEvery { interactor.findMissingAnnouncement(match { it.lastDateTime != null }) } returns flowOf(nextMissing to null)
        coEvery { interactor.getInfoAboutPet("pet-error", 0) } returns (null to InternetStatus.Error)

        val viewModel = FilterViewModel(interactor)
        listOf(
            TimeFilter.WEEK to "На неделе",
            TimeFilter.MONTH to "Месяц",
            TimeFilter.YESTERDAY to "Вчера",
            TimeFilter.THREE_DAYS to "3 дня"
        ).forEach { (filter, text) ->
            viewModel.setTime(filter)
            advanceUntilIdle()
            assertTrue(viewModel.chips.value.any { it.text == text })
        }
        viewModel.setType(9)
        viewModel.setGender(7)
        viewModel.clearChip("unknown")
        advanceUntilIdle()
        assertTrue(viewModel.chips.value.any { it.text == "Другое" })
        assertTrue(viewModel.chips.value.any { it.text == "Пол: 7" })

        viewModel.loadMoreFoundPets()
        advanceUntilIdle()
        assertTrue(viewModel.foundResults.value.isEmpty())

        viewModel.findFoundPets()
        advanceUntilIdle()
        assertIs<SearchState.Error>(viewModel.foundState.value)

        viewModel.findMissingPets()
        advanceUntilIdle()
        assertEquals(20, viewModel.missingResults.value.size)
        assertTrue(viewModel.hasMoreMissing.value)
        viewModel.loadMoreMissingPets()
        advanceUntilIdle()
        assertEquals(21, viewModel.missingResults.value.size)
        assertFalse(viewModel.hasMoreMissing.value)
        viewModel.loadMoreMissingPets()
        advanceUntilIdle()
        assertEquals(21, viewModel.missingResults.value.size)

        viewModel.getInfoAboutPet("pet-error")
        advanceUntilIdle()
        assertEquals(PetDetailsScreenState.Failed("Что-то пошло не так.."), viewModel.petInfoState.value)
        viewModel.resetPetInfoState()
        assertEquals(PetDetailsScreenState.Loading, viewModel.petInfoState.value)
    }

    @Test
    fun `filter view model ignores duplicate load more while loading`() = runTest {
        val interactor = mockk<SearchInteractor>()
        val firstFound = List(20) { petPreview(id = "found-$it", createdAt = "2026-05-28T12:00:${it.toString().padStart(2, '0')}Z") }
        val firstMissing = List(20) { petPreview(id = "missing-$it", createdAt = "2026-05-28T13:00:${it.toString().padStart(2, '0')}Z") }
        val foundGate = CompletableDeferred<Unit>()
        val missingGate = CompletableDeferred<Unit>()
        coEvery { interactor.findFoundAnnouncement(match { it.lastDateTime == null }) } returns flowOf(firstFound to null)
        coEvery { interactor.findFoundAnnouncement(match { it.lastDateTime != null }) } returns flow {
            foundGate.await()
            emit(emptyList<PetUiPreview>() to null)
        }
        coEvery { interactor.findMissingAnnouncement(match { it.lastDateTime == null }) } returns flowOf(firstMissing to null)
        coEvery { interactor.findMissingAnnouncement(match { it.lastDateTime != null }) } returns flow {
            missingGate.await()
            emit(emptyList<PetUiPreview>() to null)
        }

        val viewModel = FilterViewModel(interactor)
        viewModel.findFoundPets()
        viewModel.findMissingPets()
        advanceUntilIdle()

        viewModel.loadMoreFoundPets()
        viewModel.loadMoreFoundPets()
        viewModel.loadMoreMissingPets()
        viewModel.loadMoreMissingPets()
        foundGate.complete(Unit)
        missingGate.complete(Unit)
        advanceUntilIdle()

        coVerify(exactly = 1) { interactor.findFoundAnnouncement(match { it.lastDateTime != null }) }
        coVerify(exactly = 1) { interactor.findMissingAnnouncement(match { it.lastDateTime != null }) }
    }

    @Test
    fun `report view model emits success errors and spotted data`() = runTest {
        val interactor = mockk<SearchInteractor>()
        val uri = mockk<Uri>()
        coEvery { interactor.reportFoundAnimal("ok") } returns Response.SUCCESS
        coEvery { interactor.reportFoundAnimal("net") } returns Response.INTERNET_ERROR
        coEvery { interactor.reportSpottedAnimal("spot", any()) } returns Response.SERVER_ERROR
        val viewModel = ReportViewModel(interactor)

        viewModel.reportFoundAnimal("ok")
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isSuccess)

        viewModel.effect.test {
            viewModel.reportFoundAnimal("net")
            assertEquals(ReportFoundAnimalEffect.InternetError, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.updateLatitude(56.0)
        viewModel.updateLongitude(92.0)
        viewModel.addImage(uri)
        viewModel.effect.test {
            viewModel.reportSpottedAnimal("spot")
            assertEquals(ReportFoundAnimalEffect.ServerError, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(listOf(uri), viewModel.spottedAnimalData.value.uri)
    }

    private fun streetDetails() = StreetDetails(
        imagePath = listOf("image.jpg"),
        creator = CreatorDetails("1", "Алина"),
        placeDescription = "рядом с домом",
        lon = 92.0,
        lat = 56.0,
        dateInfo = "28/05 • 12:00"
    )

    private fun petPreview(id: String, createdAt: String) = PetUiPreview(
        id = id,
        breed = "дворовая",
        description = "описание",
        district = "Центральный",
        imageUrl = "image.jpg",
        createdAt = createdAt
    )

    private fun foundPetInfo() = FoundPetInfo(
        street = "Мира",
        house = "1",
        district = "Центральный",
        imagePath = "image.jpg",
        creator = Creator("1", "Алина"),
        petInfo = PetInfo(0, 1, "рыжий", "сибирская", "описание"),
        lon = 92.0,
        lat = 56.0,
        dateInfo = DateInfo("12:00", "28/05")
    )
}
