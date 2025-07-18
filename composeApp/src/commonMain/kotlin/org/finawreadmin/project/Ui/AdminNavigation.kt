package org.finawreadmin.project.Ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AdminNavigation(navController: NavHostController) {
    NavHost(navController, startDestination = "dashboard") {
        composable("dashboard") { DashboardScreen() }
        composable("content") { ContentManagementScreen() }
        composable("user") { UserManagementScreen() }
    }
}
