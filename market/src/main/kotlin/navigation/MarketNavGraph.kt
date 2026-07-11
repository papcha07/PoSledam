package navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import org.koin.androidx.compose.koinViewModel
import ui.MarketProductDetailsScreen
import ui.MarketScreen
import ui.MarketSellerScreen
import ui.viewModel.MarketCatalogViewModel
import ui.viewModel.MarketProductDetailsViewModel
import ui.viewModel.MarketSellerViewModel

sealed class MarketRoute(val route: String) {
    data object Main : MarketRoute("marketMain")

    data object ProductDetails : MarketRoute("marketProduct/{productId}") {
        fun createRoute(productId: String): String = "marketProduct/$productId"
    }

    data object SellerDetails : MarketRoute("marketSeller/{sellerId}") {
        fun createRoute(sellerId: String): String = "marketSeller/$sellerId"
    }
}

fun NavGraphBuilder.marketNavGraph(navController: NavController, route: String = "market") {
    navigation(
        startDestination = MarketRoute.Main.route,
        route = route
    ) {
        composable(MarketRoute.Main.route) {
            val viewModel: MarketCatalogViewModel = koinViewModel()
            MarketScreen(
                viewModel = viewModel,
                openProduct = { productId ->
                    navController.navigate(MarketRoute.ProductDetails.createRoute(productId))
                }
            )
        }

        composable(
            route = MarketRoute.ProductDetails.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
            val viewModel: MarketProductDetailsViewModel = koinViewModel()
            MarketProductDetailsScreen(
                productId = productId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                openSeller = { sellerId ->
                    navController.navigate(MarketRoute.SellerDetails.createRoute(sellerId))
                }
            )
        }

        composable(
            route = MarketRoute.SellerDetails.route,
            arguments = listOf(navArgument("sellerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val sellerId = backStackEntry.arguments?.getString("sellerId") ?: return@composable
            val viewModel: MarketSellerViewModel = koinViewModel()
            MarketSellerScreen(
                sellerId = sellerId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                openProduct = { productId ->
                    navController.navigate(MarketRoute.ProductDetails.createRoute(productId))
                }
            )
        }
    }
}
