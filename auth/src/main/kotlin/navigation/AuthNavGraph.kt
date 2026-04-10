package navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import org.koin.androidx.compose.koinViewModel
import ui.AuthViewModel
import ui.EnterScreen
import ui.OnBoardingScreen
import ui.login.LoginScreen
import ui.register.RegisterScreen

sealed class AuthRoute(val route: String) {
    object OnBoarding : AuthRoute("onBoarding")
    object Login : AuthRoute("login")
    object Register : AuthRoute("register")
    object EnterScreen : AuthRoute("enterScreen")
}

fun NavGraphBuilder.authNavGraph(navController: NavController, route: String = "auth") {
    navigation(
        startDestination = AuthRoute.OnBoarding.route,
        route = route
    ) {
        composable(AuthRoute.OnBoarding.route) {
            OnBoardingScreen(
                navigate = {
                    navController.navigate(AuthRoute.EnterScreen.route)
                }
            )
        }

        composable(AuthRoute.EnterScreen.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(route)
            }
            val authViewModel: AuthViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            EnterScreen(
                authViewModel = authViewModel,
                navigateToLoginScreen = {
                    navController.navigate(AuthRoute.Login.route)
                },
                navigateToRegisterScreen = {
                    navController.navigate(AuthRoute.Register.route)
                }
            )
        }

        composable(AuthRoute.Login.route) {
            LoginScreen() {
                navController.navigate("main") {
                    popUpTo("auth") { inclusive = true }
                    launchSingleTop = true
                }
            }
        }

        composable(AuthRoute.Register.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(route)
            }
            val authViewModel: AuthViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

            RegisterScreen(
                viewModel = authViewModel,
                goToLoginScreen = {
                    navController.navigate(AuthRoute.Login.route)
                },
                goPreviewScreen = {
                    navController.popBackStack()
                }
            )
        }
    }
}


