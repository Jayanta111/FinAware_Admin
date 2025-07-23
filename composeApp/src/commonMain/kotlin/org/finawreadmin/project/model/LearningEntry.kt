package org.finawreadmin.project.model

import kotlinx.serialization.Serializable


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
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "courseId" to courseId,
            "title" to title,
            "imageUrl" to imageUrl,
            "intro" to intro,
            "example" to example,
            "prevention" to prevention,
            "quiz" to quiz,
            "language" to language
        )
    }
}