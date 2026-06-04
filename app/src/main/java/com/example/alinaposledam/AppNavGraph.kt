package com.example.alinaposledam

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
import navigation.authNavGraph
import navigation.mainNavGraph
import navigation.profileNavGraph
import navigation.searchNavGraph
import org.koin.compose.koinInject
import org.koin.java.KoinJavaComponent.getKoin
import storage.TokenRepository

private val bottomBarLeafRoutes = setOf(
    "mainScreen",
    "searchMain",
    "profileMain"
)


@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomBarLeafRoutes

    var startDestination by remember { mutableStateOf<String?>(null) }
    val koin = getKoin()


    var isAuthorized by remember { mutableStateOf(false) }
    var hasLocationPermission by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val tokenRepository: TokenRepository = koin.get()
        val token = tokenRepository.getToken()

        if (token.isNullOrBlank()) {
            startDestination = "auth"
        } else {
            startDestination = "main"
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

