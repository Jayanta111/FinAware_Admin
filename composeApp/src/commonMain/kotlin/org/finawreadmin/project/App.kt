package org.finawreadmin.project

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import org.finawreadmin.project.Ui.AdminDashboardScreen
import org.finawreadmin.project.auth.AdminLoginScreen

@Composable
fun App() {
    MaterialTheme {
        AdminAppRoot()
    }
}

@Composable
fun AdminAppRoot() {
    var isLoggedIn by remember { mutableStateOf(false) }

    if (isLoggedIn) {
        AdminDashboardScreen()
    } else {
        AdminLoginScreen(
            onLoginSuccess = {
                isLoggedIn = true
            }
        )
    }
}


