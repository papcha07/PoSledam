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
import ui.screen.ActionScreen
import ui.screen.ProfileAnnouncementDetailsProvider
import ui.screen.ProfileScreen
import ui.screen.ProfileSettingsScreen
import ui.viewModel.ActionViewModel
import ui.viewModel.ProfileAnnouncementDetailsViewModel
import ui.viewModel.ProfileSettingsViewModel
import ui.viewModel.ProfileViewModel

sealed class ProfileRoute(val route: String) {
    object Profile : ProfileRoute("profileMain")
    object ProfileSettings : ProfileRoute("settings")
    object ActionScreen : ProfileRoute("actionScreen")
    object DetailScreen : ProfileRoute("detailScreen/{petId}/{announcementType}") {
        fun createRoute(
            petId: String,
            announcementType: Int = 1
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

            ProfileScreen(
                navigateToActionScreen = {
                    navController.navigate(ProfileRoute.ActionScreen.route)
                },
                profileViewModel = profileViewModel,
                openAnnouncementDetails = { id, type ->
                    navController.navigate(
                        ProfileRoute.DetailScreen.createRoute(
                            petId = id,
                            announcementType = type
                        )
                    )
                },
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
                    actionViewModel.clearState()
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
                navArgument("petId") { type = NavType.StringType },
                navArgument("announcementType") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getString("petId") ?: return@composable
            val announcementType =
                backStackEntry.arguments?.getInt("announcementType") ?: return@composable

            val detailsViewModel: ProfileAnnouncementDetailsViewModel = koinViewModel()

            ProfileAnnouncementDetailsProvider(
                announcementId = petId,
                announcementType = announcementType,
                viewModel = detailsViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
