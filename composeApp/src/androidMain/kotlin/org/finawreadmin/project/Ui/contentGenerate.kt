package org.finawreadmin.project.Ui

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import kotlinx.coroutines.launch
import org.finawreadmin.project.Database.ContentRepository
import org.finawreadmin.project.model.LearningEntry
import org.finawreadmin.project.API.callGemini
import java.util.UUID

@Composable
fun ContentGeneratorScreen() {
    var tabIndex by remember { mutableStateOf(0) } // 0 = AI, 1 = Manual
    var topic by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var saveMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> selectedImageUri = uri }

    Column(Modifier.padding(20.dp)) {
        Text("AI Content Generator", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        // Language Switch
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                setAppLocale(context, "en")
                (context as? Activity)?.recreate()
            }) { Text("English") }

            Button(onClick = {
                setAppLocale(context, "hi")
                (context as? Activity)?.recreate()
            }) { Text("हिन्दी") }

            Button(onClick = {
                setAppLocale(context, "pa")
                (context as? Activity)?.recreate()
            }) { Text("ਪੰਜਾਬੀ") }
        }

        Spacer(Modifier.height(16.dp))

        TabRow(selectedTabIndex = tabIndex) {
            Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }) {
                Text("Generate via AI", modifier = Modifier.padding(12.dp))
            }
            Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }) {
                Text("Manual Entry", modifier = Modifier.padding(12.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = topic,
            onValueChange = { topic = it },
            label = { Text("Enter Topic") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        when (tabIndex) {
            0 -> {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            loading = true
                            content = callGemini("Write a short, clear educational content on the topic: $topic") ?: "❌ Failed to generate content."
                            loading = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = topic.isNotBlank()
                ) {
                    Text("Generate Content")
                }

                if (loading) {
                    Spacer(Modifier.height(16.dp))
                    CircularProgressIndicator()
                }

                if (content.isNotBlank()) {
                    Spacer(Modifier.height(16.dp))
                    Text(content, modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp))
                }
            }

            1 -> {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Enter Content") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    maxLines = 10
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("Select Image")
        }

        selectedImageUri?.let { uri ->
            Spacer(Modifier.height(8.dp))
            Text("Selected Image Preview:")
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    loading = true

                    val imageUrl = selectedImageUri?.let { uri ->
                        val storage = Firebase.storage
                        val imageRef = storage.reference.child("learning_images/${UUID.randomUUID()}.jpg")
                        val uploadTask = imageRef.putFile(uri).await()
                        if (uploadTask.task.isSuccessful) {
                            imageRef.downloadUrl.await().toString()
                        } else null
                    }

                    val result = ContentRepository.saveContent(
                        LearningEntry(topic, content, imageUrl)
                    )
                    saveMessage = if (result.isSuccess)
                        "✅ Content saved!" else "❌ Failed: ${result.exceptionOrNull()?.message}"
                    loading = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = topic.isNotBlank() && content.isNotBlank()
        ) {
            Text("Save to Firebase")
        }

        saveMessage?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.primary)
        }
    }
}
