package org.finawreadmin.project

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import org.finawreadmin.project.Ui.AdminDashboardScreen

// import org.finAware.project.Ui.AdminLoginScreen // Removed for now

@Composable
fun App() {
    MaterialTheme {
        AdminDashboardScreen() // 🚀 Directly show dashboard
    }
}
