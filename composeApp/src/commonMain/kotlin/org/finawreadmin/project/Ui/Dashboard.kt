package org.finawreadmin.project.Ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AdminDashboardScreen() {
    var selectedTab by remember { mutableStateOf("Dashboard") }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (selectedTab) {
                "Dashboard" -> Text("Welcome to Admin Dashboard", fontSize = 26.sp)
                "Content" -> Text("Manage Learning Content", fontSize = 26.sp)
                "User" -> Text("Bank Authorized Users", fontSize = 26.sp)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(selectedTab: String, onTabSelected: (String) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedTab == "Dashboard",
            onClick = { onTabSelected("Dashboard") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
            label = { Text("Dashboard") }
        )
        NavigationBarItem(
            selected = selectedTab == "Content",
            onClick = { onTabSelected("Content") },
            icon = { Icon(Icons.Default.MenuBook, contentDescription = "Content") },
            label = { Text("Content") }
        )
        NavigationBarItem(
            selected = selectedTab == "User",
            onClick = { onTabSelected("User") },
            icon = { Icon(Icons.Default.Person, contentDescription = "User") },
            label = { Text("User") }
        )
    }
}
