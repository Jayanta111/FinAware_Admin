package org.finawreadmin.project.Database

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import org.finawreadmin.project.model.LearningEntry

object ContentRepository {
    suspend fun saveContent(entry: LearningEntry): Result<Unit> {
        return try {
            val db = Firebase.firestore
            db.collection("learningContent").add(entry).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
