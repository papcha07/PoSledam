package navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import org.koin.androidx.compose.koinViewModel
import ui.DetailsPetScreenProvider
import ui.SearchScreen
import ui.components.FiltersScreen
import ui.profile.PersonDto
import ui.profile.SearchProfileScreen
import ui.viewModel.FilterViewModel
import java.net.URLEncoder

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
            val announcementType =
                backStackEntry.arguments?.getInt("announcementType") ?: return@composable


            DetailsPetScreenProvider(
                viewModel = filtersViewModel,
                petId = petId,
                reportViewModel = koinViewModel(),
                announcementType = announcementType,
                goBackClick = { navController.popBackStack() },

                onOwnerClick = { creator ->
                    val name = URLEncoder.encode(creator.firstName, "UTF-8")
                    val avatarPath = (creator.avatarPath ?: "").trimStart('/')
                    val avatar = URLEncoder.encode(avatarPath, "UTF-8")
                    navController.navigate("searchProfile?name=$name&avatar=$avatar")
                }
            )
        }

        composable(
            route = "${SearchRoute.ProfileScreen.route}?name={name}&avatar={avatar}&desc={}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("avatar") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                },
                navArgument("description") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = false
                }

            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val avatar = backStackEntry.arguments?.getString("avatar")
            val description = backStackEntry.arguments?.getString("description")

            val personDto = PersonDto(
                name = name,
                uri = "TODO()",
                description = description,
                vkUri = "vk.com",
                tgUri = "tg.com"
            )
            SearchProfileScreen(
                personDto = personDto
            )

        }
    }
}