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
    var saving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var saved by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val contentGenerator = remember(apiKey) { ContentGeneratorAI(apiKey) }
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
                onValueChange = {
                    title = it
                    error = null
                    saved = false
                },
                label = { Text("Enter Lesson Title (e.g. UPI Scam, Loan Fraud)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = language,
                onValueChange = {
                    language = it
                    error = null
                    saved = false
                },
                label = { Text("Language Code (en, hi, pa, as)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Generate button (only generates, does not save)
            Button(
                onClick = {
                    scope.launch {
                        loading = true
                        error = null
                        result = null
                        saved = false

                        try {
                            val generated = contentGenerator.generateLearningEntry(
                                title = title,
                                language = language,
                                courseId = courseId
                            )

                            if (generated != null) {
                                result = generated
                            } else {
                                error = "⚠️ Failed to generate content. Try another title."
                            }
                        } catch (e: Exception) {
                            error = "🚫 Unexpected error: ${e.message}"
                        } finally {
                            loading = false
                        }
                    }
                },
                enabled = title.isNotBlank() && !loading
            ) {
                Text(if (loading) "Generating..." else "Generate Preview")
            }

            // Save button (only appears after preview)
            if (result != null && !saved) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        scope.launch {
                            saving = true
                            error = null
                            try {
                                val saveResult = repository.saveContent(courseId, result!!)
                                if (saveResult.isSuccess) {
                                    saved = true
                                } else {
                                    error = "❌ Failed to save content: ${saveResult.exceptionOrNull()?.message}"
                                }
                            } catch (e: Exception) {
                                error = "❌ Save failed: ${e.message}"
                            } finally {
                                saving = false
                            }
                        }
                    },
                    enabled = !saving
                ) {
                    Text(if (saving) "Saving..." else "Save to Database")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (saved) {
                Text(
                    text = "✅ Content saved successfully!",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            result?.let {
                Divider()
                Text("📘 Title: ${it.title}", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(12.dp))

                Text("🧠 Introduction:", style = MaterialTheme.typography.titleMedium)
                Text(it.intro, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(12.dp))

                Text("📖 Example / Case Study:", style = MaterialTheme.typography.titleMedium)
                Text(it.example, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(12.dp))

                Text("🛡️ Prevention Tips:", style = MaterialTheme.typography.titleMedium)
                Text(it.prevention, style = MaterialTheme.typography.bodyMedium)
                Divider(modifier = Modifier.padding(top = 16.dp))
            }
        }
    }
}
