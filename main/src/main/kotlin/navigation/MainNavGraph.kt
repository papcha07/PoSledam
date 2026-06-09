package navigation

import NewsScreen
import androidx.compose.runtime.remember
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

    object NewsScreen : MainRoute("newsScreen")
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
                navigateToNewsScreen = {
                    navController.navigate(MainRoute.NewsScreen.route) {
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
                            navController.navigate("detailScreen/$id/0")
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
            StreetPetRoute(
                streetPetViewModel = streetViewModel,
                openFilterSettings = {},
                returnToMainScreen = { navController.popBackStack() },
                openStreetDetails = {
                    navController.navigate(
                        MainRoute.StreetDetailsScreen.createRoute(
                            it
                        )
                    )
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
                }
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
            route = MainRoute.NewsScreen.route
        ) {
            NewsScreen(
                goBackClick = {
                    navController.popBackStack()
                }
            )
        }

    }
}

private const val MISS_ANNOUNCEMENT = 1
private const val REPORT_ANNOUNCEMENT = 0

