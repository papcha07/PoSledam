package com.example.alinaposledam

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    object AiSearch : BottomNavScreen("aiSearchMain", R.drawable.ic_ai_bottom)
    object Profile : BottomNavScreen("profileGraph", R.drawable.ic_profile)
}

private val bottomBarLeafRoutes = setOf(
    "mainScreen",
    "searchMain",
    "aiSearchMain",
    "profileMain"
)


@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        BottomNavScreen.Home,
        BottomNavScreen.Search,
        BottomNavScreen.AiSearch,
        BottomNavScreen.Profile
    )
    val selectedColor = Color(0xFF571FFF)
    val unselectedColor = Color.Gray.copy(alpha = 0.6f)

    NavigationBar(
        modifier = Modifier
            .background(shape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp), color = Color.Transparent)
            .clip(RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp)),
        containerColor = Color.White,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val destination = navBackStackEntry?.destination

        items.forEach { screen ->
            val isSelected = destination
                ?.hierarchy
                ?.any { it.route == screen.route } == true

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
                        Icon(
                            painter = painterResource(screen.icon),
                            contentDescription = null,
                            tint = if (isSelected) selectedColor else unselectedColor
                        )
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .padding(top = 32.dp)
                                    .height(3.dp)
                                    .width(20.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(selectedColor)
                                    .align(androidx.compose.ui.Alignment.BottomCenter)
                            )
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedColor,
                    unselectedIconColor = unselectedColor,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}



@Preview
@Composable
private fun BottomNavBarPreview() {
    BottomNavBar(navController = rememberNavController())
}

