package com.example.envirosense.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.envirosense.data.local.EnviroDatabase
import com.example.envirosense.data.remote.RetrofitClient
import com.example.envirosense.data.repository.EnviroRepository
import com.example.envirosense.ui.screens.dashboard.DashboardScreen
import com.example.envirosense.ui.screens.details.DetailsScreen
import com.example.envirosense.ui.screens.history.HistoryScreen
import com.example.envirosense.util.LocationHelper
import kotlinx.serialization.Serializable

// ==================== Type-safe Routes ====================

/**
 * Ekran główny - Dashboard
 */
@Serializable
object Dashboard

/**
 * Ekran historii pomiarów
 */
@Serializable
object History

/**
 * Ekran szczegółów pomiaru
 * @param measurementId ID pomiaru do wyświetlenia
 */
@Serializable
data class Details(val measurementId: Long)

// ==================== Bottom Navigation Items ====================

/**
 * Elementy nawigacji dolnej
 */
sealed class BottomNavItem<T : Any>(
    val route: T,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object DashboardItem : BottomNavItem<Dashboard>(
        route = Dashboard,
        title = "Dashboard",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )
    
    data object HistoryItem : BottomNavItem<History>(
        route = History,
        title = "Historia",
        selectedIcon = Icons.Filled.History,
        unselectedIcon = Icons.Outlined.History
    )
}

val bottomNavItems = listOf(
    BottomNavItem.DashboardItem,
    BottomNavItem.HistoryItem
)

// ==================== Main Navigation ====================

/**
 * Główny komponent nawigacji aplikacji
 */
@Composable
fun EnviroNavigation(
    database: EnviroDatabase,
    locationHelper: LocationHelper
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    // Tworzymy repozytorium
    val repository = EnviroRepository(
        measurementDao = database.measurementDao(),
        weatherApiService = RetrofitClient.weatherApiService
    )
    
    // Sprawdź czy jesteśmy na ekranie szczegółów (ukrywamy bottom nav)
    val showBottomBar = currentDestination?.hierarchy?.any { destination ->
        bottomNavItems.any { item ->
            destination.hasRoute(item.route::class)
        }
    } == true
    
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { 
                            it.hasRoute(item.route::class) 
                        } == true
                        
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    // Pop up to the start destination to avoid building up a large back stack
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies of the same destination
                                    launchSingleTop = true
                                    // Restore state when reselecting a previously selected item
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(item.title) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Dashboard,
            modifier = Modifier.padding(paddingValues)
        ) {
            // Dashboard
            composable<Dashboard> {
                DashboardScreen(
                    repository = repository,
                    locationHelper = locationHelper
                )
            }
            
            // History
            composable<History> {
                HistoryScreen(
                    repository = repository,
                    onMeasurementClick = { measurementId ->
                        navController.navigate(Details(measurementId))
                    }
                )
            }
            
            // Details
            composable<Details> { backStackEntry ->
                val details: Details = backStackEntry.toRoute()
                DetailsScreen(
                    measurementId = details.measurementId,
                    repository = repository,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
