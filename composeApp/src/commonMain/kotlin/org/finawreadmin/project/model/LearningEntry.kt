package org.finawreadmin.project.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class QuizItem(
    val question: String,
    val options: List<String>,
    val answer: String
)
@Serializable
data class Quiz(
    val title: String,
    val questions: List<QuizItem>
)
@Serializable
data class LearningEntry(
    @SerialName("_id")
    val ignoredId: String? = null,
    val courseId: String? = null,
    val title: String = "",        // ✅ Default empty string
    val imageUrl: String? = null, // ✅ Allow nulls
    val intro: String = "",        // ✅ Default empty string
    val example: String = "",      // ✅ Default empty string
    val prevention: String = "",   // ✅ Default empty string
    @Serializable(with = QuizDeserializer::class)
    val quiz: Quiz? = null,
    val language: String = ""      // ✅ Default empty string
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "courseId" to courseId,
            "title" to title,
            "imageUrl" to imageUrl,
            "intro" to intro,
            "example" to example,
            "prevention" to prevention,
            "quiz" to quiz, // ✅ now a structured Quiz object
            "language" to language
        )
    }
}
