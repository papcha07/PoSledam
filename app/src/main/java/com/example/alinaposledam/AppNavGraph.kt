package com.example.alinaposledam

import android.util.Log
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.alinaposledam.services.PushNotificationService
import com.google.firebase.messaging.FirebaseMessaging
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

private fun sendTokenToServer() {
    val notificationService = PushNotificationService()
    FirebaseMessaging.getInstance().token
        .addOnSuccessListener { token ->
            Log.d("FCM_TOKEN", "token = $token")
            notificationService.onNewToken(token)
        }
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomBarLeafRoutes

    // Определяем стартовый граф в зависимости от наличия токена
    var startDestination by remember { mutableStateOf<String?>(null) }
    val koin = getKoin()

    LaunchedEffect(Unit) {
        val tokenRepository: TokenRepository = koin.get()
        val token = tokenRepository.getToken()
        startDestination = if (token.isNullOrBlank()) {
            "auth"
        } else {
            "main"
        }

        // Отправляем FCM-токен только после инициализации
        sendTokenToServer()
    }

    Scaffold(
        bottomBar = { if (showBottomBar) BottomNavBar(navController) },
        containerColor = Color.White
    ) { innerPadding ->
        if (startDestination == null) {
            // Простой сплэш-плейсхолдер, пока решаем, куда идти
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

