package org.finawreadmin.project.Ui

import android.os.Build
import android.widget.Toast
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
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.Inet4Address
import java.net.NetworkInterface

@Serializable
data class QuizQuestion(val question: String, val options: List<String>, val correctAnswerIndex: Int)

@Serializable
data class QuizWrapper(val title: String, val questions: List<QuizQuestion>)

@Serializable
data class CombinedContentQuiz(
    val courseId: String,
    val title: String,
    val imageUrl: String,
    val intro: String,
    val example: String,
    val prevention: String,
    val language: String,
    val quiz: String
)

@Composable
actual fun ContentGeneratorScreen(navController: NavController, courseId: String) {
    val context = LocalContext.current

    val supportedLanguages = listOf("en", "hi", "pn", "as")
    var selectedLanguage by remember { mutableStateOf("en") }

    val titles = remember { mutableStateMapOf<String, String>() }
    val intros = remember { mutableStateMapOf<String, String>() }
    val examples = remember { mutableStateMapOf<String, String>() }
    val preventions = remember { mutableStateMapOf<String, String>() }

    var imageUrl by remember { mutableStateOf<String?>(null) }

    var questionText by remember { mutableStateOf("") }
    var options by remember { mutableStateOf(listOf("", "", "", "")) }
    var correctAnswerIndex by remember { mutableStateOf(0) }
    var quizTitle by remember { mutableStateOf("Quiz") }
    val questionsList = remember { mutableStateListOf<QuizQuestion>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Add Learning Content", style = MaterialTheme.typography.titleLarge)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
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

        Divider()

        Text("Quiz Builder", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = quizTitle,
            onValueChange = { quizTitle = it },
            label = { Text("Quiz Title") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = questionText,
            onValueChange = { questionText = it },
            label = { Text("Question") },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Options", style = MaterialTheme.typography.labelLarge)

        options.forEachIndexed { index, option ->
            OutlinedTextField(
                value = option,
                onValueChange = { newValue ->
                    options = options.toMutableList().also { it[index] = newValue }
                },
                label = { Text("Option ${index + 1}") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        DropdownMenuBox(
            selectedIndex = correctAnswerIndex,
            onAnswerSelected = { correctAnswerIndex = it }
        )

        Button(
            onClick = {
                if (questionText.isNotBlank() && options.all { it.isNotBlank() }) {
                    questionsList.add(QuizQuestion(questionText, options, correctAnswerIndex))
                    questionText = ""
                    options = listOf("", "", "", "")
                    correctAnswerIndex = 0
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Add Question")
        }

        Divider()

        Text("Preview Questions", style = MaterialTheme.typography.titleMedium)
        if (questionsList.isEmpty()) {
            Text("No questions added yet.")
        } else {
            questionsList.forEachIndexed { index, q ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Q${index + 1}: ${q.question}", style = MaterialTheme.typography.bodyLarge)
                        q.options.forEachIndexed { i, opt ->
                            Text("${'A' + i}: $opt${if (i == q.correctAnswerIndex) " ✅" else ""}")
                        }
                    }
                }
            }
        }

        Divider()

        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val ip = getLocalIpAddress()
                        val baseUrl = if (Build.FINGERPRINT.contains("generic"))
                            "http://${ip ?: "10.0.2.2"}:8080"
                        else
                            "https://finaware-backend.onrender.com"

                        val quizJson = Json.encodeToString(
                            QuizWrapper(title = quizTitle, questions = questionsList.toList())
                        )

                        val payload = CombinedContentQuiz(
                            courseId = courseId,
                            title = titles[selectedLanguage] ?: "",
                            imageUrl = imageUrl ?: "",
                            intro = intros[selectedLanguage] ?: "",
                            example = examples[selectedLanguage] ?: "",
                            prevention = preventions[selectedLanguage] ?: "",
                            language = selectedLanguage,
                            quiz = quizJson
                        )

                        val client = HttpClient(CIO) {
                            install(ContentNegotiation) { json() }
                        }

                        val response: HttpResponse = client.post("$baseUrl/content") {
                            contentType(ContentType.Application.Json)
                            setBody(payload)
                        }

                        withContext(Dispatchers.Main) {
                            if (response.status.isSuccess()) {
                                Toast.makeText(context, "✅ Content submitted!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, "❌ Error: ${response.status}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "❌ Exception: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit All")
        }

        Spacer(Modifier.height(24.dp))

        Text("🈯 Multi-language Preview", style = MaterialTheme.typography.titleMedium)
        supportedLanguages.forEach { lang ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
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

@Composable
fun DropdownMenuBox(selectedIndex: Int, onAnswerSelected: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text("Correct Answer: Option ${selectedIndex + 1}")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            (0..3).forEach { index ->
                DropdownMenuItem(
                    onClick = {
                        onAnswerSelected(index)
                        expanded = false
                    },
                    text = { Text("Option ${index + 1}") }
                )
            }
        }
    }
}

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
