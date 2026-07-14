package com.example.alinaposledam

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.core.R

sealed class BottomNavScreen(val route: String, @DrawableRes val icon: Int) {
    object Home : BottomNavScreen("mainScreen", R.drawable.ic_home_bottom)
    object Search : BottomNavScreen("searchMain", R.drawable.ic_search_bottom)
    object AiSearch : BottomNavScreen("aiSearchMain", R.drawable.mingcute_ai_line)
    object Profile : BottomNavScreen("profileGraph", R.drawable.ic_profile)
}

private val SelectedBubbleColor = Color(0xFFECECEC)
private val IconColor = Color(0xFF1E1E1E)

/**
 * Плавающая pill-панель навигации: белая капсула с мягкой тенью,
 * выбранный пункт выделяется серым овалом за иконкой.
 */
@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        BottomNavScreen.Home,
        BottomNavScreen.Search,
        BottomNavScreen.AiSearch,
        BottomNavScreen.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val destination = navBackStackEntry?.destination

    Box(
        modifier = Modifier
            .fillMaxWidth()
            // Подложка панели по макету.
            .background(Color(0xFFF9F9F9))
            .navigationBarsPadding()
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        // Компактная капсула по центру, как на референсе.
        Row(
            modifier = Modifier
                .shadow(
                    elevation = 10.dp,
                    shape = CircleShape,
                    clip = false,
                    spotColor = Color(0xFF292929).copy(alpha = 0.18f),
                    ambientColor = Color(0xFF292929).copy(alpha = 0.10f)
                )
                .clip(CircleShape)
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { screen ->
                val isSelected = destination
                    ?.hierarchy
                    ?.any { it.route == screen.route } == true

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            if (isSelected) SelectedBubbleColor else Color.Transparent
                        )
                        .clickable {
                            if (!isSelected) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                        .padding(horizontal = 22.dp, vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(screen.icon),
                        contentDescription = null,
                        tint = IconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}


@Preview
@Composable
private fun BottomNavBarPreview() {
    BottomNavBar(navController = rememberNavController())
}
