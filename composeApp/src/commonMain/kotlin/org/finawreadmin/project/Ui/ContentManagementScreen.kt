package org.finawreadmin.project.Ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import io.ktor.client.HttpClient
import org.finawreadmin.project.API.fetchAllContent
import org.finawreadmin.project.model.LearningEntry
import kotlin.time.Clock.System

@Composable
fun ContentManagementScreen(
    navController: NavHostController,
    client: HttpClient
) {
    val contentList = remember { mutableStateListOf<LearningEntry>() }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val fetched = fetchAllContent(client)
            contentList.clear()
            contentList.addAll(fetched)
        } catch (e: Exception) {
            errorMessage = "Failed to fetch content: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Manage Learning Content",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                
                
                onClick = {
                    val allIds = contentList.mapNotNull { entry ->
                        entry.courseId?.removePrefix("course_")?.toIntOrNull()
                    }

                    val maxId = allIds.maxOrNull() ?: 0
                    val nextId = "course_" + (maxId + 1).toString().padStart(3, '0')
                    navController.navigate("contentGenerator/$nextId")

                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Add Manual Content")
            }

            Button(
                onClick = {    val allIds = contentList.mapNotNull { entry ->
                    entry.courseId?.removePrefix("course_")?.toIntOrNull()
                }

                    val maxId = allIds.maxOrNull() ?: 0
                    val nextId = "course_" + (maxId + 1).toString().padStart(3, '0')
                    navController.navigate("ContentGeneratorAI/$nextId")
                          },
                modifier = Modifier.weight(1f)
            ) {
                Text("Add AI Content")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            errorMessage != null -> {
                Text(
                    text = errorMessage ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }

            else -> {
                Text(
                    text = "Generated Content:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(contentList) { entry ->
                        ContentCard(entry = entry)
                    }
                }
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

            entry.imageUrl?.let { url ->
                Image(
                    painter = rememberAsyncImagePainter(url),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
            }



            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Language: ${entry.language}", style = MaterialTheme.typography.bodySmall)
        }
    }
}