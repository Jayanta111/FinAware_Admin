//package org.finawreadmin.project.Ui
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//import io.ktor.client.*
//import io.ktor.client.call.*
//import io.ktor.client.engine.cio.*
//import io.ktor.client.plugins.contentnegotiation.*
//import io.ktor.client.request.*
//import io.ktor.client.statement.*
//import io.ktor.http.*
//import io.ktor.serialization.kotlinx.json.*
//import kotlinx.coroutines.launch
//import kotlinx.serialization.Serializable
//import kotlinx.serialization.json.Json
//
//// -------------------- Data Models --------------------
//
//@Serializable
//data class QuizQuestion(
//    val question: String,
//    val options: List<String>,
//    val correctAnswerIndex: Int
//)
//
//@Serializable
//data class QuizPayload(
//    val courseId: String,
//    val title: String,
//    val questions: List<QuizQuestion>
//)
//
//// -------------------- Composable UI --------------------
//
//@Composable
//fun QuizBuilderScreen(courseId: String, title: String, navController: NavController) {
//    var question by remember { mutableStateOf("") }
//    var option1 by remember { mutableStateOf("") }
//    var option2 by remember { mutableStateOf("") }
//    var option3 by remember { mutableStateOf("") }
//    var option4 by remember { mutableStateOf("") }
//    var correctAnswerIndex by remember { mutableStateOf(0) }
//
//    var questionsList by remember { mutableStateOf(mutableListOf<QuizQuestion>()) }
//    var isSubmitting by remember { mutableStateOf(false) }
//    var showError by remember { mutableStateOf(false) }
//
//    val snackbarHostState = remember { SnackbarHostState() }
//    val scope = rememberCoroutineScope()
//
//    Scaffold(
//        snackbarHost = { SnackbarHost(snackbarHostState) }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//                .padding(16.dp)
//                .verticalScroll(rememberScrollState())
//        ) {
//            Text("Create Quiz for: $title", style = MaterialTheme.typography.headlineSmall)
//
//            Spacer(Modifier.height(20.dp))
//
//            OutlinedTextField(value = question, onValueChange = { question = it }, label = { Text("Enter Question") }, modifier = Modifier.fillMaxWidth())
//            Spacer(Modifier.height(10.dp))
//
//            Text("Options:")
//            OutlinedTextField(value = option1, onValueChange = { option1 = it }, label = { Text("Option 1") }, modifier = Modifier.fillMaxWidth())
//            OutlinedTextField(value = option2, onValueChange = { option2 = it }, label = { Text("Option 2") }, modifier = Modifier.fillMaxWidth())
//            OutlinedTextField(value = option3, onValueChange = { option3 = it }, label = { Text("Option 3") }, modifier = Modifier.fillMaxWidth())
//            OutlinedTextField(value = option4, onValueChange = { option4 = it }, label = { Text("Option 4") }, modifier = Modifier.fillMaxWidth())
//
//            Spacer(Modifier.height(10.dp))
//
//            Text("Select Correct Answer:")
//            Column {
//                listOf(option1, option2, option3, option4).forEachIndexed { index, _ ->
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        RadioButton(selected = correctAnswerIndex == index, onClick = { correctAnswerIndex = index })
//                        Text("Option ${index + 1}")
//                    }
//                }
//            }
//
//            if (showError) {
//                Text("All fields must be filled and one correct option selected.", color = MaterialTheme.colorScheme.error)
//            }
//
//            Spacer(Modifier.height(16.dp))
//
//            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
//                Button(onClick = {
//                    if (question.isNotBlank() && option1.isNotBlank() && option2.isNotBlank() &&
//                        option3.isNotBlank() && option4.isNotBlank()) {
//                        val options = listOf(option1, option2, option3, option4)
//                        questionsList.add(QuizQuestion(question, options, correctAnswerIndex))
//                        // Clear
//                        question = ""; option1 = ""; option2 = ""; option3 = ""; option4 = ""
//                        correctAnswerIndex = 0
//                        showError = false
//                    } else {
//                        showError = true
//                    }
//                }) {
//                    Text("Add Question")
//                }
//
//                if (questionsList.isNotEmpty()) {
//                    Button(onClick = { questionsList.clear() }) {
//                        Text("Clear All")
//                    }
//                }
//            }
//
//            Spacer(Modifier.height(20.dp))
//
//            if (questionsList.isNotEmpty()) {
//                Text("Preview (${questionsList.size}):", style = MaterialTheme.typography.titleMedium)
//                Spacer(Modifier.height(10.dp))
//                questionsList.forEachIndexed { index, q ->
//                    Text("${index + 1}. ${q.question}")
//                    q.options.forEachIndexed { i, opt ->
//                        val prefix = if (i == q.correctAnswerIndex) "✓" else "-"
//                        Text("   $prefix ${i + 1}. $opt")
//                    }
//                    Spacer(Modifier.height(8.dp))
//                }
//
//                Spacer(Modifier.height(24.dp))
//
//                Button(
//                    onClick = {
//                        isSubmitting = true
//                        scope.launch {
//                            val success = submitQuiz(courseId, title, questionsList)
//                            isSubmitting = false
//                            if (success) {
//                                snackbarHostState.showSnackbar("✅ Quiz saved!")
//                                navController.popBackStack()
//                            } else {
//                                snackbarHostState.showSnackbar("❌ Failed to submit quiz")
//                            }
//                        }
//                    },
//                    enabled = !isSubmitting
//                ) {
//                    if (isSubmitting) {
//                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
//                        Spacer(Modifier.width(8.dp))
//                        Text("Submitting...")
//                    } else {
//                        Text("Save Quiz")
//                    }
//                }
//            }
//        }
//    }
//}
//
//// -------------------- Ktor Quiz Submission --------------------
//
//suspend fun submitQuiz(courseId: String, title: String, questions: List<QuizQuestion>): Boolean {
//    val client = HttpClient(CIO) {
//        install(ContentNegotiation) {
//            json(Json {
//                ignoreUnknownKeys = true
//                prettyPrint = true
//                isLenient = true
//            })
//        }
//    }
//
//    return try {
//        val payload = QuizPayload(courseId = courseId, title = title, questions = questions)
//
//        println("📤 SUBMITTING TO BACKEND:\n${Json.encodeToString(QuizPayload.serializer(), payload)}")
//
//        val response: HttpResponse = client.post("https://finaware-backend.onrender.com/create-quiz") {
//            contentType(ContentType.Application.Json)
//            accept(ContentType.Application.Json)
//            setBody(payload)
//        }
//
//        val responseBody = response.bodyAsText()
//        println("✅ RESPONSE STATUS: ${response.status}")
//        println("📥 RESPONSE BODY: $responseBody")
//
//        response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Created
//    } catch (e: Exception) {
//        println("❌ Quiz submission failed: ${e.message}")
//        false
//    } finally {
//        client.close()
//    }
//}
