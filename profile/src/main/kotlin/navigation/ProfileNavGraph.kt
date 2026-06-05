package navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import org.koin.androidx.compose.koinViewModel
import ui.DetailsPetScreenProvider
import ui.screen.ActionScreen
import ui.screen.ProfileScreen
import ui.screen.ProfileSettingsScreen
import ui.viewModel.ActionViewModel
import ui.viewModel.FilterViewModel
import ui.viewModel.ProfileSettingsViewModel
import ui.viewModel.ProfileViewModel

sealed class ProfileRoute(val route: String) {
    object Profile : ProfileRoute("profileMain")
    object ProfileSettings : ProfileRoute("settings")
    object ActionScreen : ProfileRoute("actionScreen")
    object DetailScreen : ProfileRoute("detailScreen/{petId}/{announcementType}") {
        fun createRoute(
            petId: String,
            announcementType: Int = 0
        ): String {
            return "detailScreen/$petId/$announcementType"
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.profileNavGraph(navController: NavController, route: String = "profileGraph") {
    navigation(
        startDestination = ProfileRoute.Profile.route,
        route = route
    ) {
        composable(ProfileRoute.Profile.route) { backStackEntry ->

            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(route)
            }

            val profileViewModel: ProfileViewModel =
                koinViewModel(viewModelStoreOwner = parentEntry)

            val profileSettingsViewModel: ProfileSettingsViewModel =
                koinViewModel(viewModelStoreOwner = parentEntry)

            ProfileScreen(
                navigateToActionScreen = {
                    navController.navigate(ProfileRoute.ActionScreen.route)
                },
                openProfileSettings = {
                    navController.navigate(ProfileRoute.ProfileSettings.route)
                },
                profileViewModel = profileViewModel,
                profileSettingsViewModel = profileSettingsViewModel
            )
        }
        composable(ProfileRoute.ActionScreen.route) { backStackEntry ->

            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(route)
            }

            val actionViewModel: ActionViewModel =
                koinViewModel(viewModelStoreOwner = parentEntry)


            ActionScreen(
                viewModel = actionViewModel,
                onProfilePage = {
                    navController.popBackStack()
                },
            )
        }

        composable(ProfileRoute.ProfileSettings.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(route)
            }

            val profileSettingsViewModel: ProfileSettingsViewModel =
                koinViewModel(viewModelStoreOwner = parentEntry)

            ProfileSettingsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                settingsViewModel = profileSettingsViewModel,
                exit = {
                    navController.navigate("auth") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }

                }
            )
        }

        composable(
            route = ProfileRoute.DetailScreen.route,
            arguments = listOf(
                navArgument("petId") { NavType.StringType },
                navArgument("announcementType") { NavType.IntType }
            )
        ) { backStackEntry ->

            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(route)
            }

            val filterViewModel: FilterViewModel = koinViewModel(
                viewModelStoreOwner = parentEntry
            )

            val petId = backStackEntry.arguments?.getString("petId") ?: return@composable
            val announcementType =
                backStackEntry.arguments?.getInt("announcementType") ?: return@composable


            DetailsPetScreenProvider(
                viewModel = filterViewModel,
                reportViewModel = koinViewModel(),
                petId = petId,
                announcementType = announcementType,
                goBackClick = { navController.popBackStack() },
                onOwnerClick = { }
            )
        }
    }
}
