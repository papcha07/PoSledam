package navigation

import android.net.Uri
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

sealed class SearchRoute(val route: String) {
    object SearchScreen : SearchRoute("searchMain")
    object FiltersScreen : SearchRoute("filtersScreen")
    data object FoundPetScreen : SearchRoute(
        route = "foundPetScreen/{petId}/{announcementType}"
    ) {
        fun createRoute(
            petId: String,
            announcementType: Int
        ): String {
            return "foundPetScreen/$petId/$announcementType"
        }
    }

    object ProfileScreen : SearchRoute("searchProfile") {
        fun createRoute(
            name: String,
            avatar: String?,
            description: String?,
            tg: String?,
            vk: String?,
            wh: String?
        ): String {
            val baseRoute = "$route/${Uri.encode(name.ifBlank { "Пользователь" })}"
            val queryParams = listOfNotNull(
                avatar.toQueryParam("avatar"),
                description.toQueryParam("desc"),
                tg.toQueryParam("tg"),
                vk.toQueryParam("vk"),
                wh.toQueryParam("wh")
            )

            return if (queryParams.isNotEmpty()) {
                "$baseRoute?${queryParams.joinToString("&")}"
            } else {
                baseRoute
            }
        }

        private fun String?.toQueryParam(name: String): String? {
            val value = this?.takeIf { it.isNotBlank() } ?: return null
            return "$name=${Uri.encode(value)}"
        }
    }
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
                    navController.navigate(
                        SearchRoute.ProfileScreen.createRoute(
                            name = creator.firstName,
                            avatar = creator.avatarPath,
                            description = creator.description,
                            tg = creator.tg,
                            vk = creator.vk,
                            wh = creator.wh
                        )
                    )
                }
            )
        }

        composable(
            route = "${SearchRoute.ProfileScreen.route}/{name}?avatar={avatar}&desc={description}&tg={tg}&vk={vk}&wh={wh}",
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
                    nullable = true
                },
                navArgument("tg") {
                    type = NavType.StringType
                    defaultValue = null
                    nullable = true
                },

                navArgument("vk") {
                    type = NavType.StringType
                    defaultValue = null
                    nullable = true
                },

                navArgument("wh") {
                    type = NavType.StringType
                    defaultValue = null
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val avatar = backStackEntry.arguments?.getString("avatar")
            val description = backStackEntry.arguments?.getString("description")
            val tg = backStackEntry.arguments?.getString("tg")
            val vk = backStackEntry.arguments?.getString("vk")
            val wh = backStackEntry.arguments?.getString("wh")

            val personDto = PersonDto(
                name = name,
                uri = avatar.blankToNull(),
                description = description.blankToNull(),
                vkUri = vk.blankToNull(),
                tgUri = tg.blankToNull(),
                whUri = wh.blankToNull()
            )
            SearchProfileScreen(
                personDto = personDto,
                returnFromProfile = {
                    navController.popBackStack()
                }
            )

        }
    }
}

private fun String?.blankToNull(): String? {
    return this?.takeIf { it.isNotBlank() }
}
