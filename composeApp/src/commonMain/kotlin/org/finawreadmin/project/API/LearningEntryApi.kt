package org.finawreadmin.project.API

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import org.finawreadmin.project.model.LearningEntry
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.serialization.kotlinx.json.json

// You can inject a platform-specific engine if needed
val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
        )
    }
}

suspend fun postLearningEntry(entry: LearningEntry): String? {
    val response = client.post("https://finaware-backend.onrender.com/content") {
        contentType(ContentType.Application.Json)
        setBody(entry)
    }

    return if (response.status == HttpStatusCode.Created) {
        val createdEntry = response.body<LearningEntry>() // or Map<String, String> if raw
        println("✅ Created Course: ${createdEntry.courseId}")
        createdEntry.courseId
    } else {
        println("❌ Error: ${response.status}")
        null
    }
}
