package com.example.alinaposledam

import android.content.Intent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.alinaposledam.firebase.FirebaseTokenProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import navigation.ProfileRoute
import navigation.authNavGraph
import navigation.mainNavGraph
import navigation.profileNavGraph
import navigation.searchNavGraph
import org.koin.java.KoinJavaComponent.getKoin
import storage.TokenRepository


private val bottomBarLeafRoutes = setOf(
    "mainScreen",
    "searchMain",
    "profileMain"
)


@Composable
fun AppNavGraph(
    initialIntent: Intent? = null,
    notificationIntents: Flow<Intent> = emptyFlow()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomBarLeafRoutes

    var startDestination by remember { mutableStateOf<String?>(null) }

    var pendingNotificationIntent by remember {
        mutableStateOf<Intent?>(initialIntent.takeIf { it.isNotificationIntent() })
    }

    val koin = getKoin()

    LaunchedEffect(Unit) {
        val tokenRepository: TokenRepository = koin.get()
        val token = tokenRepository.getToken()

        val firebaseTokenProvider: FirebaseTokenProvider = koin.get()

        if (token.isNullOrBlank()) {
            startDestination = "auth"
        } else {
            firebaseTokenProvider.sendCurrentTokenToServer()
            startDestination = "main"
        }
    }

    LaunchedEffect(Unit) {
        notificationIntents.collect { intent ->
            if (intent.isNotificationIntent()) {
                pendingNotificationIntent = intent
            }
        }
    }

    LaunchedEffect(startDestination, pendingNotificationIntent) {
        if (startDestination == "main" && pendingNotificationIntent != null) {
            handleNotificationIntent(
                intent = pendingNotificationIntent,
                navController = navController
            )

            pendingNotificationIntent = null
        }
    }

    Scaffold(
        bottomBar = { if (showBottomBar) BottomNavBar(navController) },
        containerColor = Color.White
    ) { innerPadding ->
        if (startDestination == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding()),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            NavHost(
                navController = navController,
                startDestination = startDestination!!,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding()),
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { ExitTransition.None },
            ) {
                authNavGraph(navController)
                mainNavGraph(navController)
                profileNavGraph(navController)
                searchNavGraph(navController)
            }
        }
    }
}

private const val ACTION_OPEN_FROM_NOTIFICATION = "OPEN_FROM_NOTIFICATION"

private fun Intent?.isNotificationIntent(): Boolean {
    return this?.action == ACTION_OPEN_FROM_NOTIFICATION
}

private fun handleNotificationIntent(
    intent: Intent?,
    navController: NavController
) {
    if (!intent.isNotificationIntent()) return

    val notificationType = intent?.getStringExtra("notification_type")
    val entityId = intent?.getStringExtra("entity_id") ?: return

    when (notificationType) {
        "ReportSpotted" -> {
            navController.navigate(
                ProfileRoute.DetailScreen.createRoute(
                    petId = entityId,
                    announcementType = 0
                )
            ) {
                launchSingleTop = true
            }
        }
    }
}
