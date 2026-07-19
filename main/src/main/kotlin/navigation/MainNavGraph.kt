package navigation

import StoryScreen
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import org.koin.androidx.compose.koinViewModel
import ui.screen.NotificationScreen
import ui.screen.camera.CameraScreen
import ui.screen.camera.CameraViewModel
import ui.screen.mainScreen.MainScreen
import ui.screen.mainScreen.MainScreenViewModel
import ui.screen.street.AddStreetAnimalScreen
import ui.screen.street.StreetPetRoute
import ui.screen.street.StreetPetViewModel
import ui.screen.street.detailsScreen.StreetPetDetailRouter
import ui.model.StoryId

sealed class MainRoute(val route: String) {
    object MainScreen : MainRoute("mainScreen")
    object Notifications : MainRoute("notifications")
    object StreetPetsScreen : MainRoute("streetPets")
    object CameraScreen : MainRoute("cameraScreen")
    object PlaceAnimalScreen : MainRoute("placeAnimalScreen")
    object StreetDetailsScreen : MainRoute("streetDetailsScreen/{id}") {
        fun createRoute(id: String): String {
            return "streetDetailsScreen/$id"
        }
    }

    object StoryScreen : MainRoute("storyScreen/{storyId}") {
        fun createRoute(storyId: StoryId): String {
            return "storyScreen/${storyId.name}"
        }
    }
}


fun NavGraphBuilder.mainNavGraph(navController: NavController, route: String = "main") {
    navigation(
        startDestination = MainRoute.MainScreen.route,
        route = route
    ) {

        composable(MainRoute.MainScreen.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(route)
            }
            val mainViewModel: MainScreenViewModel =
                koinViewModel(viewModelStoreOwner = parentEntry)

            MainScreen(
                navigateToStreetPetScreen = {
                    navController.navigate(MainRoute.StreetPetsScreen.route) {
                        launchSingleTop = true
                    }
                },
                navigateToCameraScreen = {
                    navController.navigate(MainRoute.CameraScreen.route) {
                        launchSingleTop = true
                    }
                },
                navigateToStoryScreen = { storyId ->
                    navController.navigate(MainRoute.StoryScreen.createRoute(storyId)) {
                        launchSingleTop = true
                    }
                },
                goToDetailsPetScreen = { petId, announcementType ->
                    navController.navigate(SearchRoute.FoundPetScreen.createRoute(petId, announcementType)) {
                        launchSingleTop = true
                    }
                },
                mainScreenViewModel = mainViewModel
            )
        }

        composable(MainRoute.Notifications.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(route)
            }
            val mainViewModel: MainScreenViewModel =
                koinViewModel(viewModelStoreOwner = parentEntry)

            NotificationScreen(
                navigateToMainScreen = {
                    navController.popBackStack()
                },
                mainScreenViewModel = mainViewModel,
                openAnnouncement = { id, type ->
                    when (type) {

                        MISS_ANNOUNCEMENT -> {
                            navController.navigate(SearchRoute.FoundPetScreen.createRoute(id, 1))
                        }

                        REPORT_ANNOUNCEMENT -> {
                            navController.navigate(profileDetailRoute(id, MISSING_PROFILE_ANNOUNCEMENT))
                        }

                        REPORT_FOUND_ANNOUNCEMENT -> {
                            // ReportFound = завершение нейропоиска: id — это id запроса на поиск,
                            // а не объявления. Открываем экран результата умного поиска
                            // (GET /api/search/{id}); маршрут объявлен в ai_search/AiSearchNavGraph.
                            navController.navigate("aiSearchResult/$id")
                        }
                    }
                },
            )
        }

        composable(MainRoute.StreetPetsScreen.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(route)
            }
            val streetViewModel: StreetPetViewModel =
                koinViewModel(viewModelStoreOwner = parentEntry)
            val refreshKey by parentEntry.savedStateHandle
                .getStateFlow(STREET_ANIMALS_REFRESH_KEY, 0L)
                .collectAsState()
            StreetPetRoute(
                streetPetViewModel = streetViewModel,
                returnToMainScreen = { navController.popBackStack() },
                openStreetDetails = {
                    navController.navigate(
                        MainRoute.StreetDetailsScreen.createRoute(
                            it
                        )
                    )
                },
                refreshKey = refreshKey,
                onRefreshHandled = {
                    parentEntry.savedStateHandle[STREET_ANIMALS_REFRESH_KEY] = 0L
                }
            )
        }

        composable(MainRoute.CameraScreen.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(route)
            }
            val cameraViewModel: CameraViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
            CameraScreen(
                cameraViewModel = cameraViewModel,
                placeAnimal = {
                    navController.popBackStack()
                    navController.navigate(MainRoute.PlaceAnimalScreen.route)
                },
                catchAnimation = true
            )
        }

        composable(MainRoute.PlaceAnimalScreen.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(route)
            }
            val cameraViewModel: CameraViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
            AddStreetAnimalScreen(
                cameraViewModel = cameraViewModel,
                onBack = {
                    navController.popBackStack()
                    cameraViewModel.clearViewModel()
                },
                onPublished = {
                    parentEntry.savedStateHandle[STREET_ANIMALS_REFRESH_KEY] =
                        System.currentTimeMillis()
                    navController.popBackStack()
                    cameraViewModel.clearViewModel()
                }
            )
        }

        composable(
            route = MainRoute.StreetDetailsScreen.route,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val streetViewModel: StreetPetViewModel = koinViewModel()
            val id = backStackEntry.arguments?.getString("id")!!

            StreetPetDetailRouter(
                streetPetViewModel = streetViewModel,
                animalId = id,
                returnBack = { navController.popBackStack() }
            )

        }

        composable(
            route = MainRoute.StoryScreen.route,
            arguments = listOf(
                navArgument("storyId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val storyId = backStackEntry.arguments
                ?.getString("storyId")
                ?.let { storyIdName ->
                    enumValues<StoryId>().firstOrNull { it.name == storyIdName }
                }
                ?: StoryId.PetEscapes

            StoryScreen(
                goBackClick = {
                    navController.popBackStack()
                },
                storyId = storyId,
            )
        }

    }
}

private const val MISS_ANNOUNCEMENT = 1
private const val REPORT_ANNOUNCEMENT = 0
private const val REPORT_FOUND_ANNOUNCEMENT = 2
private const val MISSING_PROFILE_ANNOUNCEMENT = 0
private const val STREET_ANIMALS_REFRESH_KEY = "street_animals_refresh_key"

private fun profileDetailRoute(
    id: String,
    announcementType: Int
): String {
    return "detailScreen/$id/$announcementType"
}
