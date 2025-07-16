package org.finawreadmin.project.Ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LearningContentGeneratorScreen() {
    var topic by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(20.dp)) {
        Text("AI Content Generator", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = topic,
            onValueChange = { topic = it },
            label = { Text("Enter Topic Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        Button(onClick = {
            loading = true
            CoroutineScope(Dispatchers.Default).launch {
                val result = callGemini("You are  a expert in finacial advicer and fraud detection your role is  learning guide on: $topic ")
                content = result ?: "Failed to generate content."
                loading = false
            }
        }) {
            Text("Generate")
        }

        if (loading) {
            Spacer(Modifier.height(10.dp))
            CircularProgressIndicator()
        }

        if (content.isNotEmpty()) {
            Spacer(Modifier.height(20.dp))
            Text("Generated Content:", style = MaterialTheme.typography.titleLarge)
            Text(content, modifier = Modifier.padding(top = 10.dp))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LearningContentGeneratorScreenPreview() {
    MaterialTheme {
        LearningContentGeneratorScreen()
    }
}
