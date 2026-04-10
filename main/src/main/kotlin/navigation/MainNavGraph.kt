package navigation

import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import ui.NotificationScreen
import ui.cameraScreen.CameraScreen
import ui.cameraScreen.CameraViewModel
import ui.mainScreen.MainScreen
import ui.mainScreen.MainScreenViewModel
import ui.other.DebouncerManager
import ui.streetScreen.AddStreetAnimalScreen
import ui.streetScreen.StreetPetScreen
import ui.streetScreen.StreetPetViewModel

sealed class MainRoute(val route: String) {
    object MainScreen : MainRoute("mainScreen")
    object Notifications : MainRoute("notifications")
    object StreetPetsScreen : MainRoute("streetPets")
    object CameraScreen : MainRoute("cameraScreen")
    object PlaceAnimalScreen : MainRoute("placeAnimalScreen")
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

            val scope = rememberCoroutineScope()
            val delay = 5000L

            val debouncerManager = koinInject<DebouncerManager>(
                parameters = { parametersOf(scope, delay) }
            )

            MainScreen(
                navigateToNotificationScreen = {
                    debouncerManager.debounce {
                        navController.navigate(MainRoute.Notifications.route) {
                            launchSingleTop = true
                        }
                    }
                },
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
                mainScreenViewModel = mainViewModel
            )
        }

        composable(MainRoute.StreetPetsScreen.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(route)
            }
            val streetViewModel: StreetPetViewModel =
                koinViewModel(viewModelStoreOwner = parentEntry)
            StreetPetScreen(
                streetPetViewModel = streetViewModel
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
                }
            )
        }


    }
}