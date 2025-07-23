package org.finawreadmin.project.API

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.finawreadmin.project.model.LearningEntry

// Define your data class model (must match the JSON schema from Express/MongoDB)
@Serializable
data class LearningEntry(
    val courseId: String,
    val title: String,
    val imageUrl: String?,
    val intro: String,
    val example: String,
    val prevention: String,
    val quiz: String,
    val language: String
)

suspend fun fetchAllContent(client: HttpClient): List<LearningEntry> {
    return try {
        val response: HttpResponse = client.get("https://finaware-backend.onrender.com/content") {
            accept(ContentType.Application.Json)
        }

        if (response.status == HttpStatusCode.OK) {
            response.body()
        } else {
            println("❌ HTTP ${response.status}")
            println("Body: ${response.bodyAsText()}")
            emptyList()
        }
    } catch (e: Exception) {
        println("❌ Exception while fetching content: ${e.message}")
        emptyList()
    }
}