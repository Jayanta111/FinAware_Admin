package org.finawreadmin.project.Ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class User(val name: String, val email: String)

@Composable
fun UserManagementScreen() {
    val authorizedUsers = remember {
        listOf(
            User("Ramesh Kumar", "ramesh@bank.com"),
            User("Anita Sharma", "anita@bank.com"),
            User("John Doe", "john@bank.com")
        )
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text(
            " Users ",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .padding(vertical = 12.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Name",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                fontSize = 16.sp
            )
            Text(
                text = "Email",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(2f),
                fontSize = 16.sp
            )
        }

        Divider(color = Color.Gray.copy(alpha = 0.5f), thickness = 1.dp)

        // Table Body
        LazyColumn {
            items(authorizedUsers) { user ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = user.name,
                        modifier = Modifier.weight(1f),
                        fontSize = 15.sp
                    )
                    Text(
                        text = user.email,
                        modifier = Modifier.weight(2f),
                        fontSize = 15.sp
                    )
                }
                Divider(color = Color.LightGray.copy(alpha = 0.4f))
            }
        }
    }
}
