package org.finawreadmin.project.Ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.finawreadmin.project.model.LearningEntry

@Composable
fun ContentManagementScreen(navController: NavHostController) {
    // TODO: Replace with actual Firestore data fetch
    val generatedContent = remember {
        mutableStateListOf(
            LearningEntry(title = "What is UPI?", body = "Unified Payments Interface explained..."),
            LearningEntry(title = "Avoiding Scams", body = "Tips to stay safe from phishing and fraud.")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Manage Learning Content",
            fontSize = 26.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                navController.navigate("contentGenerator")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Add Generated Content")
        }

        Text(
            text = "Generated Content:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(generatedContent) { entry ->
                ContentCard(entry)
            }
        }
    }
}

@Composable
fun ContentCard(entry: LearningEntry) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = entry.title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = entry.body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
