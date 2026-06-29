package navigation

import android.net.Uri
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.navArgument
import org.koin.androidx.compose.koinViewModel
import ui.email_confirmation.EmailConfirmationRoute
import ui.login.LoginRoute
import ui.other.EnterScreen
import ui.other.OnBoardingScreen
import ui.privacy.PrivacyPolicyScreen
import ui.register.RegisterScreen
import ui.register.RegisterViewModel

sealed class AuthRoute(val route: String) {
    object OnBoarding : AuthRoute("onBoarding")
    object Login : AuthRoute("login")
    object Register : AuthRoute("register")
    object EnterScreen : AuthRoute("enterScreen")
    object PrivacyPolicy : AuthRoute("privacyPolicy")
    object EmailConfirmation : AuthRoute("emailConfirmation/{email}") {
        fun createRoute(email: String): String {
            return "emailConfirmation/${Uri.encode(email)}"
        }
    }
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
            val registerViewModel: RegisterViewModel =
                koinViewModel(viewModelStoreOwner = parentEntry)

            EnterScreen(
                registerViewModel = registerViewModel,
                navigateToLoginScreen = {
                    navController.navigate(AuthRoute.Login.route)
                },
                navigateToRegisterScreen = {
                    navController.navigate(AuthRoute.Register.route)
                }
            )
        }

        composable(AuthRoute.Login.route) {
            LoginRoute(
                goToMainProfile = {
                    navController.navigate("main") {
                        popUpTo("auth") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                goToPrivacyPolicy = {
                    navController.navigate(AuthRoute.PrivacyPolicy.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(AuthRoute.Register.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(route)
            }
            val registerViewModel: RegisterViewModel =
                koinViewModel(viewModelStoreOwner = parentEntry)

            RegisterScreen(
                registerViewModel = registerViewModel,
                goToEmailConfirmationScreen = { email ->
                    navController.navigate(AuthRoute.EmailConfirmation.createRoute(email)) {
                        popUpTo(AuthRoute.Register.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                goPreviewScreen = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = AuthRoute.EmailConfirmation.route,
            arguments = listOf(
                navArgument("email") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments
                ?.getString("email")
                ?.let(Uri::decode)
                .orEmpty()

            EmailConfirmationRoute(
                email = email,
                goToLoginScreen = {
                    navController.navigate(AuthRoute.Login.route) {
                        popUpTo(route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(AuthRoute.PrivacyPolicy.route) {
            PrivacyPolicyScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
