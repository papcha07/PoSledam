package navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import org.koin.androidx.compose.koinViewModel
import ui.DetailPetScreen
import ui.FilterViewModel
import ui.components.FiltersScreen
import ui.components.SearchScreen

sealed class SearchRoute(val route: String) {
    object SearchScreen : SearchRoute("searchMain")
    object FiltersScreen : SearchRoute("filtersScreen")
    object FoundPetScreen : SearchRoute("foundPetScreen/{petId}/{announcementType}")
    object ProfileScreen : SearchRoute("searchProfile")
}


fun NavGraphBuilder.searchNavGraph(navController: NavController, route: String = "search") {
    navigation(
        startDestination = SearchRoute.SearchScreen.route,
        route = route
    ) {
        composable(SearchRoute.SearchScreen.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(route)
            }
            val filtersViewModel: FilterViewModel =
                koinViewModel(viewModelStoreOwner = parentEntry)

            SearchScreen(
                filtersViewModel = filtersViewModel,
                goToDetailsPetScreen = { petId, announcementType ->
                    filtersViewModel.resetPetInfoState()
                    navController.navigate("foundPetScreen/$petId/$announcementType")
                },
                onActionClick = {
                    navController.navigate(SearchRoute.FiltersScreen.route)
                }
            )
        }

        composable(SearchRoute.FiltersScreen.route) { backStackEntry ->

            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(route)
            }

            val filtersViewModel: FilterViewModel =
                koinViewModel(viewModelStoreOwner = parentEntry)


            FiltersScreen(filtersViewModel = filtersViewModel) {
                navController.navigate(SearchRoute.SearchScreen.route)
            }
        }

        composable(
            route = SearchRoute.FoundPetScreen.route,
            arguments = listOf(
                navArgument("petId") { type = NavType.StringType },
                navArgument("announcementType") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(route)
            }
            val filtersViewModel: FilterViewModel =
                koinViewModel(viewModelStoreOwner = parentEntry)

            val petId = backStackEntry.arguments?.getString("petId") ?: return@composable
            val announcementType = backStackEntry.arguments?.getInt("announcementType") ?: return@composable



            DetailPetScreen(
                viewModel = filtersViewModel,
                petId = petId,
                announcementType = announcementType,
                goBackClick = { navController.popBackStack() },
                onOwnerClick = { creator ->
                    val name = java.net.URLEncoder.encode(creator.firstName, "UTF-8")
                    val avatarPath = (creator.avatarPath ?: "").trimStart('/')
                    val avatar = java.net.URLEncoder.encode(avatarPath, "UTF-8")
                    navController.navigate("searchProfile?name=$name&avatar=$avatar")
                }
            )
        }

        composable(
            route = "${SearchRoute.ProfileScreen.route}?name={name}&avatar={avatar}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("avatar") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val avatar = backStackEntry.arguments?.getString("avatar")

            val profile = ui.SearchProfileUi(
                name = name,
                description = "",
                avatarUrl = avatar?.takeIf { it.isNotBlank() },
                contacts = emptyList()
            )
            ui.SearchProfileScreen(
                profile = profile,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}