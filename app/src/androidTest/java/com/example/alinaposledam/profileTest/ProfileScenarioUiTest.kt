    package com.example.alinaposledam.profileTest

    import androidx.compose.ui.test.junit4.createComposeRule
    import androidx.compose.ui.test.onAllNodesWithTag
    import androidx.test.ext.junit.runners.AndroidJUnit4
    import kotlinx.coroutines.CompletableDeferred
    import org.junit.Before
    import org.junit.Rule
    import org.junit.Test
    import org.junit.runner.RunWith
    import org.koin.java.KoinJavaComponent.getKoin
    import ui.model.PetUiPreview
    import ui.screen.ProfileScreen
    import ui.viewModel.ProfileSettingsViewModel
    import ui.viewModel.ProfileViewModel

    @RunWith(AndroidJUnit4::class)
    class ProfileScenarioUiTest {
        @get:Rule
        val rule = createComposeRule()

        private lateinit var profileViewModel: ProfileViewModel
        private lateinit var page: ProfilePage
        private lateinit var profileSettingsViewModel: ProfileSettingsViewModel

        @Before
        fun setUp() {
            profileSettingsViewModel = getKoin().get()
            page = ProfilePage(rule)
        }

        @Test
        fun loadingStateIsShown(){
            val fake = FakeAnnouncementInteractor().apply {
                gate = CompletableDeferred()
                returnsEmpty()
            }
            launchScreen(fake)
            page.progressBarIsDisplayed()
        }

        @Test
        fun emptyStateIsShown() {
            val fake = FakeAnnouncementInteractor().apply {
                returnsEmpty()
            }

            launchScreen(fake)

            rule.waitForIdle()
            page.emptyListIsDisplayed()
        }

        @Test
        fun listStateIsShown(){
            val fake = FakeAnnouncementInteractor().apply {
                returnSuccess(listOf(PetUiPreview(
                    id = "asdasdasd",
                    breed = "Хаски",
                    description = "Очень ласковый",
                    district = "ОКТЯБРЬСКИЙ",
                    imageUrl = "asdasdasd"
                )))
            }
            launchScreen(fake)
            rule.waitForIdle()
            page.animalListIsDisplayed()
        }


        @Test
        fun progressBarBeforeAnimalList(){
            val fake = FakeAnnouncementInteractor().apply {
                gate = CompletableDeferred()
                returnsEmpty()
            }
            launchScreen(fake)
            page.progressBarIsDisplayed()
            fake.returnSuccess(listOf(PetUiPreview(
                id = "asdasdasd",
                breed = "Хаски",
                description = "Очень ласковый",
                district = "ОКТЯБРЬСКИЙ",
                imageUrl = "asdasdasd"
            )))
            rule.runOnIdle { fake.gate?.complete(Unit) }
            rule.waitForIdle()
            page.animalListIsDisplayed()
        }

        @Test
        fun loadAnimalListAndChangePage() {
            val fake = FakeAnnouncementInteractor().apply {
                gate = CompletableDeferred()
            }
            launchScreen(fake)
            page.progressBarIsDisplayed()
            fake.returnSuccess(
                listOf(
                    PetUiPreview(
                        id = "asdasdasd",
                        breed = "Хаски",
                        description = "Очень ласковый",
                        district = "ОКТЯБРЬСКИЙ",
                        imageUrl = "asdasdasd"
                    )
                )
            )
            rule.runOnIdle { fake.gate?.complete(Unit) }

            rule.waitUntil(3_000) {
                rule.onAllNodesWithTag("animal_list").fetchSemanticsNodes().isNotEmpty()
            }
            page.animalListIsDisplayed()

            rule.runOnIdle {
                fake.gate = CompletableDeferred()
                fake.returnsEmpty()
            }
            page.clickFindButton()
            page.findButtonIsSelected()
            page.progressBarIsDisplayed()
        }




        private fun launchScreen(
            fake: FakeAnnouncementInteractor
        ): ProfileViewModel {
            val vm = ProfileViewModel(fake)
            rule.setContent {
                ProfileScreen(
                    navigateToActionScreen = {},
                    openProfileSettings = {},
                    profileViewModel = vm,
                    profileSettingsViewModel = profileSettingsViewModel,
                )
            }
            return vm
        }
    }