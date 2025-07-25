package org.finawreadmin.project.Ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import io.ktor.client.HttpClient
import org.finawreadmin.project.API.fetchAllContent
import org.finawreadmin.project.model.LearningEntry

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
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    val nextId = getNextCourseId(contentList)
                    navController.navigate("contentGenerator/$nextId")
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Add Manual Content")
            }

            Button(
                onClick = {
                    val nextId = getNextCourseId(contentList)
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
                val sortedList = contentList.sortedBy {
                    it.courseId?.removePrefix("course_")?.toIntOrNull() ?: Int.MAX_VALUE
                }

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(sortedList) { entry ->
                        ContentCard(entry = entry, navController = navController)
                    }
                }
            }
        }
    }
}

private fun getNextCourseId(contentList: List<LearningEntry>): String {
    val allIds = contentList.mapNotNull { entry ->
        entry.courseId?.removePrefix("course_")?.toIntOrNull()
    }
    val maxId = allIds.maxOrNull() ?: 0
    return "course_" + (maxId + 1).toString().padStart(3, '0')
}

@Composable
fun ContentCard(entry: LearningEntry, navController: NavHostController) {
    Card(
        onClick = {
            entry.courseId?.let { courseId ->
                navController.navigate("editCourse/$courseId")
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = entry.title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1
            )

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

            Text(
                text = entry.intro,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}

