package org.finawreadmin.project.Database

import org.finawreadmin.project.model.LearningEntry
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import io.ktor.serialization.kotlinx.json.*

actual class ContentRepository {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    private val serverUrl = "https://finaware-backend.onrender.com/api/content"

    actual suspend fun saveContent(courseId: String, entry: LearningEntry): Result<Unit> {
        return try {
            val response: HttpResponse = client.post(serverUrl) {
                contentType(ContentType.Application.Json)
                setBody(entry) // Let Ktor handle serialization
            }

            if (response.status.isSuccess()) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Server error: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
