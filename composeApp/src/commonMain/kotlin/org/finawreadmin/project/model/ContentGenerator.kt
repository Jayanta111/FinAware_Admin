package org.finawreadmin.project.model


expect class ContentGeneratorAI(apiKey: String) {
    suspend fun generateLearningEntry(
        title: String,
        language: String,
        courseId: String
    ): LearningEntry?
}