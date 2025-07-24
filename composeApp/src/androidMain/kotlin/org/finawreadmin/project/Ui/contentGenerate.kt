package org.finawreadmin.project.Ui

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import io.ktor.client.request.*
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.finawreadmin.project.API.client
import org.finawreadmin.project.model.LearningEntry
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@Composable
actual fun ContentGeneratorScreen(navController: NavController, courseId: String) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var title by remember { mutableStateOf("") }
    var intro by remember { mutableStateOf("") }
    var example by remember { mutableStateOf("") }
    var prevention by remember { mutableStateOf("") }
    var quiz by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("en") }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var uploadedImageUrl by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> selectedImageUri = uri }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Content Generator", style = MaterialTheme.typography.headlineSmall)

        // --- Image Picker Section ---
        Text("1. Select & Upload Image", style = MaterialTheme.typography.titleMedium)
        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("Pick Image")
        }

        selectedImageUri?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(model = uri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Button(
                onClick = {
                    coroutineScope.launch {
                        isUploading = true
                        getFileFromUri(context, uri)?.let { file ->
                            uploadedImageUrl = uploadImage(file)
                        }
                        isUploading = false
                    }
                },
                enabled = !isUploading
            ) {
                Text(if (isUploading) "Uploading..." else "Upload Image")
            }
        }

        // --- Content Inputs ---
        Text("2. Enter Learning Content", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = intro,
            onValueChange = { intro = it },
            label = { Text("Intro") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = example,
            onValueChange = { example = it },
            label = { Text("Example") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = prevention,
            onValueChange = { prevention = it },
            label = { Text("Prevention") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = quiz,
            onValueChange = { quiz = it },
            label = { Text("Quiz (optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        // --- Language Selector ---
        Text("3. Choose Language", style = MaterialTheme.typography.titleMedium)
        LanguageSwitcher(selectedLanguage = language, onLanguageChange = { language = it })

        // --- Submit Button ---
        Button(
            onClick = {
                coroutineScope.launch {
                    isUploading = true
                    try {
                        val file = selectedImageUri?.let { getFileFromUri(context, it) }
                        if (file != null) {
                            val uploadedUrl = uploadImage(file)
                            uploadedImageUrl = uploadedUrl

                            val entry = LearningEntry(
                                title = title.trim(),
                                imageUrl = uploadedImageUrl,
                                intro = intro.trim(),
                                example = example.trim(),
                                prevention = prevention.trim(),
                                quiz = quiz.trim(),
                                language = language.trim()
                            )

                            val response: HttpResponse = client.post("https://finaware-backend.onrender.com/content") {
                                contentType(ContentType.Application.Json)
                                setBody(entry)
                            }

                            if (response.status == HttpStatusCode.Created) {
                                val responseBody = response.bodyAsText()
                                val jsonElement = Json.parseToJsonElement(responseBody)
                                val newCourseId = jsonElement.jsonObject["courseId"]?.jsonPrimitive?.content ?: ""
                                println("✅ Content created. courseId: $newCourseId")
                                navController.navigate("quizBuilder/$newCourseId/${title.trim()}")
                            } else {
                                println("❌ Failed: ${response.status}")
                            }
                        }
                    } catch (e: Exception) {
                        println("❌ Exception: ${e.message}")
                    } finally {
                        isUploading = false
                    }
                }
            },
            enabled = !isUploading && selectedImageUri != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isUploading) "Submitting..." else "Submit Content")
        }
    }


    @Composable
fun LanguageSwitcher(selectedLanguage: String, onLanguageChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Select Language", style = MaterialTheme.typography.labelSmall)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LanguageRadioButton("en", "English", selectedLanguage, onLanguageChange)
            LanguageRadioButton("hi", "Hindi", selectedLanguage, onLanguageChange)
            LanguageRadioButton("pa", "Punjabi", selectedLanguage, onLanguageChange)
            LanguageRadioButton("as", "Assamese", selectedLanguage, onLanguageChange)
        }
    }
}

@Composable
fun LanguageRadioButton(
    value: String,
    label: String,
    selectedLanguage: String,
    onSelected: (String) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = selectedLanguage == value,
            onClick = { onSelected(value) }
        )
        Text(label, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(start = 2.dp))
    }
}

fun getFileFromUri(context: Context, uri: Uri): File? {
    val mimeType = context.contentResolver.getType(uri) ?: return null
    val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: return null
    val inputStream: InputStream = context.contentResolver.openInputStream(uri) ?: return null
    val tempFile = File.createTempFile("upload_", ".$ext", context.cacheDir)
    FileOutputStream(tempFile).use { output -> inputStream.copyTo(output) }
    return tempFile
}

suspend fun uploadImage(file: File): String {
    val response = client.submitFormWithBinaryData(
        url = "https://finaware-backend.onrender.com/uploads",
        formData = formData {
            append("image", file.readBytes(), Headers.build {
                append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
            })
        }
    )

    val responseText = response.bodyAsText()
    println("🧾 Upload response: $responseText")

    if (!response.status.isSuccess()) {
        throw Exception("❌ Server error: ${response.status}\n$responseText")
    }

    return try {
        val jsonElement = Json.parseToJsonElement(responseText)
        jsonElement.jsonObject["filePath"]?.jsonPrimitive?.content ?: ""
    } catch (e: Exception) {
        throw Exception("❌ Failed to parse JSON: ${e.message}\nRaw response: $responseText")
    }
}
