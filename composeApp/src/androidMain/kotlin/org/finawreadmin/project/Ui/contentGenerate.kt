package org.finawreadmin.project.Ui

import android.app.Activity
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
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.finawreadmin.project.Database.ContentRepository
import org.finawreadmin.project.model.LearningEntry
import org.finawreadmin.project.utils.LocalAppContentHelper
import java.util.*

@Composable
actual fun ContentGeneratorScreen(navController : NavController) {
    var topic by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var saveMessage by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> selectedImageUri = uri }

    Column(modifier = Modifier.padding(20.dp)) {
        Text("Manual Content Entry", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Language Switcher (Optional)
        LanguageSwitcher()

        OutlinedTextField(
            value = topic,
            onValueChange = { topic = it },
            label = { Text("Enter Topic") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Enter Content") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            maxLines = 10
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("Select Image")
        }

        selectedImageUri?.let { uri ->
            Spacer(modifier = Modifier.height(8.dp))
            Text("Selected Image Preview:")
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    val imageUrl = selectedImageUri?.let { uri ->
                        val storage = Firebase.storage
                        val imageRef =
                            storage.reference.child("learning_images/${UUID.randomUUID()}.jpg")
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
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = topic.isNotBlank() && content.isNotBlank()
        ) {
            Text("Save to Firebase")
        }

        saveMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun LanguageSwitcher() {
    val context = LocalContext.current
    val helper = LocalAppContentHelper.current

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = {
            helper.setLocale(context, "en")
            (context as? Activity)?.recreate()
        }) {
            Text("English")
        }

        Button(onClick = {
            helper.setLocale(context, "hi")
            (context as? Activity)?.recreate()
        }) {
            Text("हिन्दी")
        }

        Button(onClick = {
            helper.setLocale(context, "pa")
            (context as? Activity)?.recreate()
        }) {
            Text("ਪੰਜਾਬੀ")
        }
    }

    Spacer(Modifier.height(16.dp))
}
