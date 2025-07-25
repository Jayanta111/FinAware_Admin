package org.finawreadmin.project.util

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import org.finawreadmin.project.model.LearningEntry

object JsonUtils {
    private val json = Json { ignoreUnknownKeys = true }

    fun fetchAllContent(jsonString: String): List<LearningEntry> {
        val jsonElements = json.parseToJsonElement(jsonString)

        if (jsonElements !is JsonArray) return emptyList()

        return jsonElements.filter { element ->
            val imageUrl = element.jsonObject["imageUrl"]
            imageUrl == null || (imageUrl is JsonPrimitive && imageUrl.isString)
        }.mapNotNull { element ->
            try {
                json.decodeFromJsonElement(LearningEntry.serializer(), element)
            } catch (e: Exception) {
                println("❌ Skipped invalid entry: ${e.message}")
                null
            }
        }
    }
}
