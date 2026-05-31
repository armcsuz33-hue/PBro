package com.avangard.stock.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.avangard.stock.ui.screens.*

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Bosh sahifa", Icons.Filled.Dashboard)
    object Products : Screen("products", "Mahsulotlar", Icons.Filled.Inventory)
    object Transactions : Screen("transactions", "Tranzaksiyalar", Icons.Filled.SwapHoriz)
    object AddTransaction : Screen("add_transaction", "Yangi amal", Icons.Filled.AddCircle)
    object Admin : Screen("admin", "Admin", Icons.Filled.AdminPanelSettings)
}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Products,
    Screen.AddTransaction,
    Screen.Transactions,
    Screen.Admin
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvangardNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title, style = MaterialTheme.typography.labelSmall) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) { DashboardScreen() }
            composable(Screen.Products.route) { ProductsScreen() }
            composable(Screen.Transactions.route) { TransactionsScreen() }
            composable(Screen.AddTransaction.route) { AddTransactionScreen() }
            composable(Screen.Admin.route) { AdminScreen() }
        }
    }
}
