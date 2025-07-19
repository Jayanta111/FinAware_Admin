package org.finawreadmin.project.Ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AdminNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "dashboard",
        modifier = modifier
    ) {
        composable("dashboard") { DashboardScreen() }
        composable("content") { ContentManagementScreen(navController) } // ✅ Fixed
        composable("user") { UserManagementScreen() }
        composable("contentGenerator") {
            ContentGeneratorScreen(navController)
        }
    }
}
