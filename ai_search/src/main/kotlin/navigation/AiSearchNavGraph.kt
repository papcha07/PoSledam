package navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import org.koin.androidx.compose.koinViewModel
import ui.AiSearchResultScreen
import ui.AiSearchScreen
import ui.viewModel.AiSearchResultViewModel
import ui.viewModel.AiSearchViewModel

sealed class AiSearchRoute(val route: String) {
    /** Стартовый экран фичи — пункт bottom navigation. */
    data object Main : AiSearchRoute("aiSearchMain")

    /** Экран результата по id запроса (открывается из push и из истории). */
    data object Result : AiSearchRoute("aiSearchResult/{requestId}") {
        fun createRoute(requestId: String): String = "aiSearchResult/$requestId"
    }
}

/**
 * Навигационный граф нейросетевого поиска.
 *
 * @param onOpenAnnouncement маппинг клика по похожему объявлению на существующие экраны деталей.
 *  Реализуется в app-модуле (там доступны SearchRoute/MainRoute), чтобы не тянуть
 *  зависимости на другие feature-модули.
 */
fun NavGraphBuilder.aiSearchNavGraph(
    navController: NavController,
    route: String = "aiSearch",
    onOpenAnnouncement: (id: String, type: Int) -> Unit
) {
    navigation(
        startDestination = AiSearchRoute.Main.route,
        route = route
    ) {
        composable(AiSearchRoute.Main.route) {
            val viewModel: AiSearchViewModel = koinViewModel()
            AiSearchScreen(
                viewModel = viewModel,
                onOpenAnnouncement = onOpenAnnouncement
            )
        }

        composable(
            route = AiSearchRoute.Result.route,
            arguments = listOf(
                navArgument("requestId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val requestId = backStackEntry.arguments?.getString("requestId") ?: return@composable
            val viewModel: AiSearchResultViewModel = koinViewModel()
            AiSearchResultScreen(
                requestId = requestId,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onOpenAnnouncement = onOpenAnnouncement
            )
        }
    }
}
