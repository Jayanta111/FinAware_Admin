package org.finawreadmin.project.Database


import org.finawreadmin.project.model.LearningEntry

expect class ContentRepository {
    suspend fun saveContent(courseId: String, entry: LearningEntry): Result<Unit>
}