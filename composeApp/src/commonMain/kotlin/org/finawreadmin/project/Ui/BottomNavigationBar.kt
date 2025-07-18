package org.finawreadmin.project.Ui

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable

@Composable
fun BottomNavigationBar(selectedTab: String, onTabSelected: (String) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedTab == "dashboard",
            onClick = { onTabSelected("dashboard") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
            label = { Text("Dashboard") }
        )
        NavigationBarItem(
            selected = selectedTab == "content",
            onClick = { onTabSelected("content") },
            icon = { Icon(Icons.Default.MenuBook, contentDescription = "Content") },
            label = { Text("Content") }
        )
        NavigationBarItem(
            selected = selectedTab == "user",
            onClick = { onTabSelected("user") },
            icon = { Icon(Icons.Default.Person, contentDescription = "User") },
            label = { Text("Users") }
        )
    }
}
