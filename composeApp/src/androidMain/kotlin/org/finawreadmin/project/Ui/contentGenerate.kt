package org.finawreadmin.project.Ui

import android.R.id.title
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*
import java.net.Inet4Address
import java.net.NetworkInterface

@Composable
actual fun ContentGeneratorScreen(navController: NavController, courseId: String) {
    val context = LocalContext.current
    val supportedLanguages = listOf("en", "hi", "pn", "as")
    var selectedLanguage by remember { mutableStateOf("en") }

    val titles = remember { mutableStateMapOf<String, String>() }
    val intros = remember { mutableStateMapOf<String, String>() }
    val examples = remember { mutableStateMapOf<String, String>() }
    val preventions = remember { mutableStateMapOf<String, String>() }

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
    }

    val client = remember {
        HttpClient() {
            install(ContentNegotiation) {
                json()
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Upload Learning Content", style = MaterialTheme.typography.titleLarge)

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            supportedLanguages.forEach { lang ->
                OutlinedButton(
                    onClick = { selectedLanguage = lang },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (lang == selectedLanguage)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(lang.uppercase())
                }
            }
        }

        OutlinedTextField(
            value = titles[selectedLanguage] ?: "",
            onValueChange = { titles[selectedLanguage] = it },
            label = { Text("Title (${selectedLanguage.uppercase()})") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = intros[selectedLanguage] ?: "",
            onValueChange = { intros[selectedLanguage] = it },
            label = { Text("Intro (${selectedLanguage.uppercase()})") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = examples[selectedLanguage] ?: "",
            onValueChange = { examples[selectedLanguage] = it },
            label = { Text("Example (${selectedLanguage.uppercase()})") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = preventions[selectedLanguage] ?: "",
            onValueChange = { preventions[selectedLanguage] = it },
            label = { Text("Prevention (${selectedLanguage.uppercase()})") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Button(onClick = { launcher.launch("image/*") }) {
            Text("Choose Image")
        }

        imageUri?.let {
            AsyncImage(
                model = it,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(8.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    val success = uploadContentWithImage(
                        context = context,
                        client = client,
                        imageUri = imageUri,
                        title = titles[selectedLanguage].orEmpty(),
                        intro = intros[selectedLanguage].orEmpty(),
                        example = examples[selectedLanguage].orEmpty(),
                        prevention = preventions[selectedLanguage].orEmpty(),
                        language = selectedLanguage,
                        courseId = courseId
                    )
                    withContext(Dispatchers.Main) {
                        if (success) {
                            Toast.makeText(context, "✅ Content uploaded!", Toast.LENGTH_SHORT).show()
                            val selectedTitle = titles[selectedLanguage].orEmpty()
                            navController.navigate("quiz_builder/${courseId}/${Uri.encode(selectedTitle)}")                        } else {
                            Toast.makeText(context, "❌ Upload failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit Content and Create Quiz")
        }

        Spacer(Modifier.height(24.dp))

        Text("🈯 Multi-language Preview", style = MaterialTheme.typography.titleMedium)
        supportedLanguages.forEach { lang ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("Language: ${lang.uppercase()}", style = MaterialTheme.typography.titleSmall)
                    Text("Title: ${titles[lang] ?: "—"}")
                    Text("Intro: ${intros[lang] ?: "—"}")
                    Text("Example: ${examples[lang] ?: "—"}")
                    Text("Prevention: ${preventions[lang] ?: "—"}")
                }
            }
        }
    }
}

suspend fun uploadContentWithImage(
    context: Context,
    client: HttpClient,
    imageUri: Uri?,
    title: String,
    intro: String,
    example: String,
    prevention: String,
    language: String,
    courseId: String
): Boolean {
    return try {
        val contentResolver = context.contentResolver
        val formData = formData {
            append("title", title)
            append("intro", intro)
            append("example", example)
            append("prevention", prevention)
            append("language", language)
            append("courseId", courseId)

            imageUri?.let { uri ->
                val inputStream = contentResolver.openInputStream(uri) ?: return@let
                val bytes = inputStream.readBytes()
                val fileName = "image_${System.currentTimeMillis()}.jpg"

                append(
                    key = "image",
                    value = bytes,
                    headers = Headers.build {
                        append(HttpHeaders.ContentDisposition, "form-data; name=\"image\"; filename=\"$fileName\"")
                        append(HttpHeaders.ContentType, "image/jpeg")
                    }
                )
            }
        }

        val response = client.submitFormWithBinaryData(
            url = "https://finaware-backend.onrender.com/content",
            formData = formData
        )

        response.status.isSuccess()
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
