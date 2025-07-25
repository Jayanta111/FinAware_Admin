package org.finawreadmin.project.model

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.finawreadmin.project.Database.ContentRepository

actual class ContentGeneratorAI actual constructor(private val apiKey: String) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    private val endpoint =
        "https://generativelanguage.googleapis.com/v1beta/models/flash-1.5:generateContent?key=$apiKey"

    actual suspend fun generateLearningEntry(
        title: String,
        language: String,
        courseId: String
    ): LearningEntry? {
        return try {
            val prompt = buildPrompt(title, language)
            val payload = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(GeminiPart(text = prompt))
                    )
                )
            )

            val response: GeminiResponse = client.post(endpoint) {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }.body()

            val generatedText =
                response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: return null

            val (intro, example, prevention) = extractSections(generatedText)

            val quiz = null
            val imageUrl = String()
            val entry = LearningEntry(
                courseId = courseId,
                title = title,
                imageUrl = imageUrl,
                intro = intro,
                example = example,
                prevention = prevention,
                quiz =  quiz,
                language = language
            )

            ContentRepository().saveContent(courseId, entry)

            return entry
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun buildPrompt(title: String, language: String): String {
        return when (language.lowercase()) {
            "hi" -> "एक वित्तीय साक्षरता पाठ बनाएं जिसका शीर्षक \"$title\" हो। इसमें तीन भाग शामिल करें: परिचय, एक उदाहरण/परिदृश्य, और रोकथाम युक्तियाँ।"
            "pa" -> "ਇੱਕ ਵਿੱਤੀ ਸਾਖਰਤਾ ਪਾਠ ਬਣਾਓ ਜਿਸਦਾ ਸਿਰਲੇਖ \"$title\" ਹੋਵੇ। ਇਹ ਤਿੰਨ ਹਿੱਸਿਆਂ 'ਚ ਵੰਡਿਆ ਹੋਇਆ ਹੋਵੇ: ਜਾਣ-ਪਛਾਣ, ਉਦਾਹਰਣ ਜਾਂ ਸਥਿਤੀ, ਅਤੇ ਰੋਕਥਾਮ ਸੁਝਾਅ।"
            else -> "Write a financial literacy lesson titled \"$title\". Structure it in 3 sections: Introduction, Example, and Prevention Tips."
        }
    }

    private fun extractSections(text: String): Triple<String, String, String> {
        val parts = text.split("\n\n").map { it.trim() }.filter { it.isNotEmpty() }
        val intro = parts.getOrNull(0) ?: ""
        val example = parts.getOrNull(1) ?: ""
        val prevention = parts.getOrNull(2) ?: ""
        return Triple(intro, example, prevention)
    }
}
