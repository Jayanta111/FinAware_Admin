package org.finawreadmin.project.Ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import org.finawreadmin.project.model.ContentGeneratorAI
import org.finawreadmin.project.model.LearningEntry
import org.finawreadmin.project.Database.ContentRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun ContentGeneratorAIScreen(
    navController: NavHostController,
    courseId: String,
    apiKey: String
) {
    var title by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("en") }
    var result by remember { mutableStateOf<LearningEntry?>(null) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val contentGenerator = remember { ContentGeneratorAI(apiKey) }
    val repository = remember { ContentRepository() }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("AI Content Generator") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Enter Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = language,
                onValueChange = { language = it },
                label = { Text("Language (en, hi, pa)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        loading = true
                        error = null
                        try {
                            // 1. Generate content using Gemini
                            val generated = contentGenerator.generateLearningEntry(title, language, courseId)
                            result = generated

                            // 2. Save generated content to backend
                            generated?.let {
                                kotlinx.coroutines.delay(500) // small delay to update UI
                                val saveResult = repository.saveContent(courseId, it)
                                if (saveResult.isFailure) {
                                    error = "Failed to save content: ${saveResult.exceptionOrNull()?.message}"
                                }
                            } ?: run {
                                error = "Content generation failed."
                            }
                        } catch (e: Exception) {
                            error = "Unexpected error: ${e.message}"
                        } finally {
                            loading = false
                        }
                    }
                },
                enabled = title.isNotBlank() && !loading
            ) {
                Text(if (loading) "Generating..." else "Generate Content")
            }

            Spacer(modifier = Modifier.height(16.dp))

            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            result?.let {
                Text("📘 Title: ${it.title}", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("📝 Intro:\n${it.intro}")
                Spacer(modifier = Modifier.height(8.dp))
                Text("📖 Example:\n${it.example}")
                Spacer(modifier = Modifier.height(8.dp))
                Text("🛡️ Prevention:\n${it.prevention}")
            }
        }
    }
}