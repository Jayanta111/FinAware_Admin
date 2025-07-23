package org.finawreadmin.project.Ui

import android.content.Context
import android.net.Uri
import android.os.Build
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
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.finawreadmin.project.model.LearningEntry
import java.net.Inet4Address
import java.net.NetworkInterface

@Composable
actual fun ContentGeneratorScreen(navController: NavController, courseId: String) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedLanguage by remember { mutableStateOf("en") }
    var title by remember { mutableStateOf("") }
    var intro by remember { mutableStateOf("") }
    var example by remember { mutableStateOf("") }
    var prevention by remember { mutableStateOf("") }
    var saveMessage by remember { mutableStateOf<String?>(null) }
    var imageUri: Uri? by remember { mutableStateOf<Uri?>(null) }
    var imageUrl by remember { mutableStateOf<String?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            imageUri = uri
            coroutineScope.launch {
                imageUrl = uploadImageToServer(uri, context)
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Manual Content Entry", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LanguageSwitcher(selectedLanguage) { selectedLanguage = it }

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Enter Title") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = intro, onValueChange = { intro = it }, label = { Text("Introduction") }, modifier = Modifier.fillMaxWidth().height(120.dp), maxLines = 6)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = example, onValueChange = { example = it }, label = { Text("Example / Scenario") }, modifier = Modifier.fillMaxWidth().height(120.dp), maxLines = 6)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = prevention, onValueChange = { prevention = it }, label = { Text("Prevention Tips") }, modifier = Modifier.fillMaxWidth().height(120.dp), maxLines = 6)

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("Select Image")
        }

        imageUri?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Selected Image:")
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                coroutineScope.launch {
                    val httpClient = HttpClient(CIO) {
                        install(ContentNegotiation) { json() }
                    }

                    try {
                        val isEmulator = Build.FINGERPRINT.contains("generic")
                        val detectedIp = getLocalIpAddress()
                        val baseUrl = if (isEmulator) "http://${detectedIp ?: "10.0.2.2"}:8080" else "https://finaware-backend.onrender.com"

                        val entry = LearningEntry(
                            courseId = courseId,
                            title = title.trim(),
                            imageUrl = imageUrl,
                            intro = intro.trim(),
                            example = example.trim(),
                            prevention = prevention.trim(),
                            quiz = "",
                            language = selectedLanguage
                        )

                        val response = httpClient.post("$baseUrl/content") {
                            contentType(ContentType.Application.Json)
                            setBody(entry)
                        }

                        if (response.status.isSuccess()) {
                            saveMessage = "✅ Content saved successfully!"
                            navController.navigate("quiz_builder/$courseId/${title.trim()}")
                        } else {
                            saveMessage = "❌ Save failed: ${response.status}"
                        }
                    } catch (e: Exception) {
                        saveMessage = "❌ Error: ${e.localizedMessage}"
                        e.printStackTrace()
                    } finally {
                        httpClient.close()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank() && intro.isNotBlank()
        ) {
            Text("Save & Add Quiz")
        }

        saveMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }

}

// Utility: Get Local IP Address (IPv4 only, non-loopback)
fun getLocalIpAddress(): String? {
    return try {
        val interfaces = NetworkInterface.getNetworkInterfaces()
        for (intf in interfaces) {
            val addrs = intf.inetAddresses
            for (addr in addrs) {
                if (!addr.isLoopbackAddress && addr is Inet4Address) {
                    return addr.hostAddress
                }
            }
        }
        null
    } catch (ex: Exception) {
        ex.printStackTrace()
        null
    }
}

@Composable
fun LanguageSwitcher(selectedLanguage: String, onLanguageChange: (String) -> Unit) {
    Column {
        Text("Select Language", style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LanguageRadioButton("en", "English", selectedLanguage, onLanguageChange)
            LanguageRadioButton("hi", "Hindi", selectedLanguage, onLanguageChange)
            LanguageRadioButton("pa", "Punjabi", selectedLanguage, onLanguageChange)
        }
    }
}

@Composable
fun LanguageRadioButton(
    code: String,
    label: String,
    selectedLanguage: String,
    onSelect: (String) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = selectedLanguage == code,
            onClick = { onSelect(code) }
        )
        Text(label)
    }
}
//Image Upload logic
suspend fun uploadImageToServer(uri: Uri?, context: Context): String? {
    if (uri == null) return null

    val inputStream = context.contentResolver.openInputStream(uri) ?: return null
    val bytes = inputStream.readBytes()
    inputStream.close()

    val fileName = "upload_${System.currentTimeMillis()}.jpg"
    val client = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = 60_000
            connectTimeoutMillis = 30_000
            socketTimeoutMillis = 60_000
        }
    }

    return try {
        val response = withTimeout(60_000) {
            client.post("https://finaware-backend.onrender.com/uploads") {
                setBody(MultiPartFormDataContent(
                    formData {
                        append("file", bytes, Headers.build {
                            append(HttpHeaders.ContentDisposition, "form-data; name=\"file\"; filename=\"$fileName\"")
                            append(HttpHeaders.ContentType, "image/jpeg")
                        })
                    }
                ))
            }
        }

        client.close()

        if (response.status.isSuccess()) {
            val body = response.bodyAsText()
            val regex = Regex("\"imageUrl\"\\s*:\\s*\"([^\"]+)\"")
            regex.find(body)?.groupValues?.get(1)
        } else {
            println("Upload failed: ${response.status}")
            null
        }
    } catch (e: Exception) {
        println("Upload error: ${e.message}")
        client.close()
        null
    }
}
